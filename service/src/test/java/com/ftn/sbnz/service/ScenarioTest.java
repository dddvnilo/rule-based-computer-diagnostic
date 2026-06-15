package com.ftn.sbnz.service;

import com.ftn.sbnz.model.*;
import com.ftn.sbnz.model.DijagnozaFakt.Ozbiljnost;
import com.ftn.sbnz.model.KvarFakt.TipKvara;
import org.junit.jupiter.api.*;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integracioni scenario testovi — pun lanac nivo1->nivo2->nivo3.
 * Svaki test simulira realan dijagnosticki slucaj: ubacuje MerenjeEvent
 * i KorisnikOdgovori, okida sva pravila i proverava krajnje DijagnozaFakt.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ScenarioTest {

    @Autowired
    private KieContainer kieContainer;

    private KieSession session;

    @BeforeEach
    void setUp() {
        session = kieContainer.newKieSession();
    }

    @AfterEach
    void tearDown() {
        session.dispose();
    }

    // --- Helpers ---

    private void fire() {
        session.fireAllRules(match -> !match.getRule().getName().startsWith("CEP-"));
    }

    private List<DijagnozaFakt> dijagnoze() {
        return session.getObjects(new ClassObjectFilter(DijagnozaFakt.class))
                .stream().map(DijagnozaFakt.class::cast).collect(Collectors.toList());
    }

    private MerenjeEvent normalMerenje() {
        MerenjeEvent m = new MerenjeEvent();
        m.setTemperaturaCPU(55);
        m.setTemperaturaGPU(60);
        m.setTemperaturaChipseta(50);
        m.setRpmCPUVentilator(1200);
        m.setRpmGPUVentilator(1500);
        m.setRpmCaseVentilator(900);
        m.setMemtestGreske(0);
        m.setRamZauzetost(50);
        m.setSmartReallocatedSectors(0);
        m.setSmartPendingSectors(0);
        m.setSmartUncorrectableErrors(0);
        m.setDiskPowerOnHours(5000);
        m.setNapon12V(12.1);
        m.setNapon5V(5.0);
        m.setNapon3V3(3.3);
        m.setCpuUtilizacija(30);
        m.setPacketLoss(0.5);
        m.setPingMs(20);
        m.setMrezBrzinaMbps(950);
        m.setEventLogGreske(0);
        return m;
    }

    // Scenario 1: Fizicki kvar RAM — BSOD MEMORY_MANAGEMENT + memtest greske

    @Test
    void scenario_fizicki_kvar_ram() {
        // Korisnik prijavljuje BSOD sa MEMORY_MANAGEMENT kodom
        // Memtest pokazuje 5 gresaka na RAM modulima
        MerenjeEvent m = normalMerenje();
        m.setMemtestGreske(5);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setBsod(true);
        o.setBsodKod("MEMORY_MANAGEMENT");

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        // FIZICKI_KVAR_RAM mora biti dijagnostikovan
        DijagnozaFakt ramDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.FIZICKI_KVAR_RAM)
                .findFirst()
                .orElseThrow(() -> new AssertionError("FIZICKI_KVAR_RAM nije dijagnostikovan"));

        assertEquals(Ozbiljnost.KRITICNO, ramDijagnoza.getOzbiljnost(),
                "Fizicki kvar RAM mora biti KRITICNO");

        // Nema dijagnoze koja je nizi nivo od KRITICNO za isti kvar
        assertFalse(rezultat.stream()
                .anyMatch(d -> d.getKvar().getTipKvara() == TipKvara.FIZICKI_KVAR_RAM
                        && d.getOzbiljnost() != Ozbiljnost.KRITICNO),
                "Ne sme biti duplikata dijagnoze za RAM sa razlicitom ozbiljnoscu");
    }

    // Scenario 2: Degradiran ventilator CPU — RPM u kriticnom opsegu

    @Test
    void scenario_kvar_ventilatora_visoka_temperatura() {
        // CPU ventilator radi na 300 RPM (degradiran, 100-500 opseg)
        // CPU temperatura povisena, korisnik cuje neobicne zvukove i oseca pregrevanje
        MerenjeEvent m = normalMerenje();
        m.setRpmCPUVentilator(300);
        m.setTemperaturaCPU(91);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setNeobicniZvukovi(true);
        o.setPregrevanje(true);

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        // KVAR_VENTILATORA je ocekivana dijagnoza (RPM 100-500 = degradiran, ne stao)
        DijagnozaFakt ventDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.KVAR_VENTILATORA)
                .findFirst()
                .orElseThrow(() -> new AssertionError("KVAR_VENTILATORA nije dijagnostikovan"));

        assertEquals(Ozbiljnost.KRITICNO, ventDijagnoza.getOzbiljnost());

        // VENTILATOR_STAO ne sme biti prisutan — RPM je 300, ne 0
        assertTrue(rezultat.stream()
                .noneMatch(d -> d.getKvar().getTipKvara() == TipKvara.VENTILATOR_STAO),
                "VENTILATOR_STAO ne sme biti dijagnostikovan kada je RPM 300 (fan degradiran, nije stao)");
    }

    // Scenario 3: Nestabilan PSU kao koren uzrok visestrukih kvarova

    @Test
    void scenario_nestabilan_psu_kao_koren_uzrok() {
        // Napon 12V pada na 10.8V, 5V linija na 4.5V
        // GPU pokazuje artefakte (efekt loseg napajanja), korisnik prijavljuje restartove
        MerenjeEvent m = normalMerenje();
        m.setNapon12V(10.8);
        m.setNapon5V(4.5);
        m.setTemperaturaGPU(97);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setUcestaliRestartovi(true);
        o.setArtefaktiNaEkranu(true);

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        // NESTABILAN_NAPON mora biti dijagnostikovan sa KRITICNO i porukom o korenu uzroka
        DijagnozaFakt psuDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.NESTABILAN_NAPON)
                .findFirst()
                .orElseThrow(() -> new AssertionError("NESTABILAN_NAPON nije dijagnostikovan"));

        assertEquals(Ozbiljnost.KRITICNO, psuDijagnoza.getOzbiljnost());
        assertTrue(psuDijagnoza.getPreporuka().contains("KOREN UZROK"),
                "PSU dijagnoza uz visestruke kvarove treba da sadrzi 'KOREN UZROK' u preporuci");

        // GPU kvar mora biti detektovan kao sekundarni efekat loseg napajanja
        assertTrue(rezultat.stream()
                .anyMatch(d -> d.getKvar().getTipKvara() == TipKvara.PREGREVANJE_GPU),
                "PREGREVANJE_GPU (temp=97) treba biti detektovan kao sekundarni kvar uz nestabilan PSU");

        // Ukupno 2 dijagnoze: PSU (koren uzrok) + GPU (sekundarni)
        assertEquals(2, rezultat.size(),
                "Ocekivane su tacno 2 dijagnoze: NESTABILAN_NAPON i PREGREVANJE_GPU");
    }

    // Scenario 4: Pregrevanje CPU pod opterecenjem

    @Test
    void scenario_pregrevanje_cpu_pod_opterecenjem() {
        // CPU se pregreva (94C) pri visokom opterecenju (96% util)
        // Ventilator radi normalno (1200 RPM) — ne radi se o kvaru hladjenja
        // Ocekivano: PREGREVANJE_CPU (KRITICNO) + ISTROSENA_TERMALNA_PASTA (UPOZORENJE)
        // jer visoka temp uz uredan RPM ukazuje i na istrosenu termopaste
        MerenjeEvent m = normalMerenje();
        m.setTemperaturaCPU(94);
        m.setCpuUtilizacija(96);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setPregrevanje(true);
        o.setSporRad(true);

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        DijagnozaFakt cpuDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.PREGREVANJE_CPU)
                .findFirst()
                .orElseThrow(() -> new AssertionError("PREGREVANJE_CPU nije dijagnostikovan"));
        assertEquals(Ozbiljnost.KRITICNO, cpuDijagnoza.getOzbiljnost());

        // Uz visoku temp i normalan RPM, sistem ispravno zakljucuje da je i termalna pasta istrosena
        DijagnozaFakt pastaDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.ISTROSENA_TERMALNA_PASTA)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ISTROSENA_TERMALNA_PASTA nije dijagnostikovan"));
        assertEquals(Ozbiljnost.UPOZORENJE, pastaDijagnoza.getOzbiljnost());
    }

    // Scenario 5: Istrosenost diska — INFO ozbiljnost

    @Test
    void scenario_istrosenost_diska_info_ozbiljnost() {
        // Disk ima 45000 radnih sati (prag je 30000h)
        // SMART greske su nula — disk jos radi, ali blizi se kraju zivotnog veka
        // Ocekivano: ISTROSENOST_DISKA sa INFO ozbiljnoscu (ne KRITICNO)
        MerenjeEvent m = normalMerenje();
        m.setDiskPowerOnHours(45000);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setSporRad(true);

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        DijagnozaFakt diskDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.ISTROSENOST_DISKA)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ISTROSENOST_DISKA nije dijagnostikovan"));

        assertEquals(Ozbiljnost.INFO, diskDijagnoza.getOzbiljnost(),
                "Istrosenost diska treba biti INFO — disk jos radi, samo preventivno upozorenje");

        // Nema LOSI_SEKTORI_DISK jer su SMART parametri normalni
        assertTrue(rezultat.stream()
                .noneMatch(d -> d.getKvar().getTipKvara() == TipKvara.LOSI_SEKTORI_DISK),
                "Bez SMART gresaka ne sme biti dijagnoze o losim sektorima");
    }

    // Scenario 6: GPU VRAM kvar — artefakti bez pregrevanja

    @Test
    void scenario_vram_kvar_artefakti_bez_pregrevanja() {
        // GPU radi na normalnoj temperaturi (72C), ali postoje vizuelni artefakti
        // Artefakti uz normalnu temperaturu ukazuju na kvar VRAM memorije, ne na pregrevanje
        MerenjeEvent m = normalMerenje();
        m.setTemperaturaGPU(72);

        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setArtefaktiNaEkranu(true);

        session.insert(m);
        session.insert(o);
        fire();

        List<DijagnozaFakt> rezultat = dijagnoze();

        DijagnozaFakt vramDijagnoza = rezultat.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.VRAM_KVAR)
                .findFirst()
                .orElseThrow(() -> new AssertionError("VRAM_KVAR nije dijagnostikovan"));

        assertEquals(Ozbiljnost.KRITICNO, vramDijagnoza.getOzbiljnost());

        // PREGREVANJE_GPU ne sme biti prisutno — GPU temperatura je normalna (72C < 90C)
        assertTrue(rezultat.stream()
                .noneMatch(d -> d.getKvar().getTipKvara() == TipKvara.PREGREVANJE_GPU),
                "PREGREVANJE_GPU ne sme biti dijagnostikovan pri normalnoj GPU temperaturi (72 stepen)");
    }
}
