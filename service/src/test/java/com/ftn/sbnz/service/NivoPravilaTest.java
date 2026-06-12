package com.ftn.sbnz.service;

import com.ftn.sbnz.model.*;
import com.ftn.sbnz.model.DijagnozaFakt.Ozbiljnost;
import com.ftn.sbnz.model.KomponentaFakt.TipKomponente;
import com.ftn.sbnz.model.KvarFakt.TipKvara;
import org.junit.jupiter.api.*;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class NivoPravilaTest {

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

    private <T> List<T> wm(Class<T> clazz) {
        return session.getObjects(new ClassObjectFilter(clazz))
                .stream().map(clazz::cast).collect(Collectors.toList());
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

    private MerenjeEvent merenje(Consumer<MerenjeEvent> setup) {
        MerenjeEvent m = normalMerenje();
        setup.accept(m);
        return m;
    }

    private KorisnikOdgovori odgovori(Consumer<KorisnikOdgovori> setup) {
        KorisnikOdgovori o = new KorisnikOdgovori();
        setup.accept(o);
        return o;
    }

    
    // Nivo 1 - prepoznavanje komponenti
    

    @Test
    void nivo1_simptom_pregrevanje_prepoznaje_cpu_i_cooling() {
        session.insert(normalMerenje());
        session.insert(odgovori(o -> o.setPregrevanje(true)));
        fire();

        List<TipKomponente> komponente = wm(KomponentaFakt.class).stream()
                .map(KomponentaFakt::getTipKomponente).collect(Collectors.toList());
        assertTrue(komponente.contains(TipKomponente.CPU));
        assertTrue(komponente.contains(TipKomponente.COOLING));
    }

    @Test
    void nivo1_merenje_visoka_temperatura_cpu_prepoznaje_cpu() {
        session.insert(merenje(m -> m.setTemperaturaCPU(95)));
        fire();

        List<TipKomponente> komponente = wm(KomponentaFakt.class).stream()
                .map(KomponentaFakt::getTipKomponente).collect(Collectors.toList());
        assertTrue(komponente.contains(TipKomponente.CPU));
    }

    @Test
    void nivo1_bsod_bez_koda_prepoznaje_os() {
        session.insert(normalMerenje());
        KorisnikOdgovori o = new KorisnikOdgovori();
        o.setBsod(true);
        // bsodKod je null - treba da okine pravilo "bsod -> OS"
        session.insert(o);
        fire();

        List<TipKomponente> komponente = wm(KomponentaFakt.class).stream()
                .map(KomponentaFakt::getTipKomponente).collect(Collectors.toList());
        assertTrue(komponente.contains(TipKomponente.OS));
    }

    @Test
    void nivo1_bsod_memory_management_prepoznaje_ram() {
        session.insert(normalMerenje());
        session.insert(odgovori(o -> {
            o.setBsod(true);
            o.setBsodKod("MEMORY_MANAGEMENT");
        }));
        fire();

        List<TipKomponente> komponente = wm(KomponentaFakt.class).stream()
                .map(KomponentaFakt::getTipKomponente).collect(Collectors.toList());
        assertTrue(komponente.contains(TipKomponente.RAM));
    }

    
    // Nivo 2 - identifikacija kvarova
    

    @Test
    void nivo2_rpm_nula_kreira_ventilator_stao_ne_kvar_ventilatora() {
        session.insert(new KomponentaFakt(TipKomponente.COOLING));
        session.insert(merenje(m -> m.setRpmCPUVentilator(0)));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.VENTILATOR_STAO));
        assertFalse(kvarovi.contains(TipKvara.KVAR_VENTILATORA),
                "Ventilator koji je stao ne sme da okine KVAR_VENTILATORA (degradiran)");
    }

    @Test
    void nivo2_rpm_degradiran_kreira_kvar_ventilatora_ne_ventilator_stao() {
        session.insert(new KomponentaFakt(TipKomponente.COOLING));
        session.insert(merenje(m -> m.setRpmCPUVentilator(300)));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.KVAR_VENTILATORA));
        assertFalse(kvarovi.contains(TipKvara.VENTILATOR_STAO),
                "Degradiran ventilator (300 RPM) ne sme okinu VENTILATOR_STAO");
    }

    @Test
    void nivo2_visoka_ram_zauzetost_kreira_preopterecenje_ram() {
        session.insert(new KomponentaFakt(TipKomponente.RAM));
        session.insert(merenje(m -> m.setRamZauzetost(92)));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.PREOPTERECENJE_RAM));
    }

    @Test
    void nivo2_smart_greske_kreira_losi_sektori_diska() {
        session.insert(new KomponentaFakt(TipKomponente.DISK));
        session.insert(merenje(m -> m.setSmartReallocatedSectors(5)));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.LOSI_SEKTORI_DISK));
    }

    
    // Nivo 3 - dijagnoza sa ozbiljnošću
    

    @Test
    void nivo3_ventilator_stao_je_kriticno() {
        KomponentaFakt k = new KomponentaFakt(TipKomponente.COOLING);
        session.insert(new KvarFakt(TipKvara.VENTILATOR_STAO, k));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.KRITICNO, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_istrosenost_diska_je_info() {
        KomponentaFakt k = new KomponentaFakt(TipKomponente.DISK);
        session.insert(new KvarFakt(TipKvara.ISTROSENOST_DISKA, k));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.INFO, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_pregrevanje_chipseta_je_upozorenje() {
        KomponentaFakt k = new KomponentaFakt(TipKomponente.MOTHERBOARD);
        session.insert(new KvarFakt(TipKvara.PREGREVANJE_CHIPSETA, k));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.UPOZORENJE, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_pregrevanje_gpu_kriticno_iznad_100_stepeni() {
        // Pun lanac: nivo1 (temp>90->GPU) -> nivo2 (temp>95->PREGREVANJE_GPU) -> nivo3 (temp>100->KRITICNO)
        // Direktno ubacivanje KvarFakt + MerenjeEvent(temp=105) bi kreiralo duplikat KvarFakt
        // jer bi nivo1+nivo2 kreirali jos jedan nezavisno od manuelno ubacenog
        session.insert(merenje(m -> m.setTemperaturaGPU(105)));
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class).stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.PREGREVANJE_GPU)
                .collect(Collectors.toList());
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.KRITICNO, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_pregrevanje_gpu_upozorenje_do_100_stepeni() {
        // temp=98: > 90 (nivo1 okida GPU), > 95 (nivo2 okida PREGREVANJE_GPU), <= 100 (nivo3 -> UPOZORENJE)
        session.insert(merenje(m -> m.setTemperaturaGPU(98)));
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class).stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.PREGREVANJE_GPU)
                .collect(Collectors.toList());
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.UPOZORENJE, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_eskalacija_losi_sektori_uz_cep2_alarm_je_kriticno_sa_specificnom_porukom() {
        KomponentaFakt k = new KomponentaFakt(TipKomponente.DISK);
        session.insert(new KvarFakt(TipKvara.LOSI_SEKTORI_DISK, k));
        // CEP alarm se ubacuje direktno kao sto DiagnosticService kopira iz cepKieSession
        session.insert(new CepAlarmFakt("CEP2_SMART_DISK", "ucestale SMART greske"));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.KRITICNO, dijagnoze.get(0).getOzbiljnost());
        // Eskalaciono pravilo daje specificniju poruku od template pravila
        assertTrue(dijagnoze.get(0).getPreporuka().contains("SMART"),
                "Eskalaciona dijagnoza treba da pominje SMART greske");
    }

    
    // Nivo 1 - dodatni testovi
    

    @Test
    void nivo1_bez_simptoma_ne_detektuje_komponente() {
        // Sva merenja normalna, nema simptoma -> ni jedna komponenta ne sme biti detektovana
        session.insert(normalMerenje());
        session.insert(new KorisnikOdgovori());
        fire();

        assertTrue(wm(KomponentaFakt.class).isEmpty(),
                "Normalna merenja bez simptoma ne smeju kreirati KomponentaFakt");
    }

    @Test
    void nivo1_bsod_page_fault_prepoznaje_ram_i_disk() {
        // PAGE_FAULT_IN_NONPAGED_AREA je jedini BSOD kod koji okida i RAM i DISK
        session.insert(normalMerenje());
        session.insert(odgovori(o -> {
            o.setBsod(true);
            o.setBsodKod("PAGE_FAULT_IN_NONPAGED_AREA");
        }));
        fire();

        List<TipKomponente> komponente = wm(KomponentaFakt.class).stream()
                .map(KomponentaFakt::getTipKomponente).collect(Collectors.toList());
        assertTrue(komponente.contains(TipKomponente.RAM));
        assertTrue(komponente.contains(TipKomponente.DISK));
    }

    
    // Nivo 2 - dodatni testovi
    

    @Test
    void nivo2_visoka_temp_sa_visokim_rpm_kreira_istrosenu_termalnu_pastu() {
        // Temp > 85 + RPM >= 500 -> ventilator radi uredeno ali CPU se pregreva = pasta istrosena
        session.insert(new KomponentaFakt(TipKomponente.COOLING));
        session.insert(merenje(m -> {
            m.setTemperaturaCPU(91); // > 90 (strogo), RPM uredan = pasta istrosena
            m.setRpmCPUVentilator(1200);
        }));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.ISTROSENA_TERMALNA_PASTA));
        assertFalse(kvarovi.contains(TipKvara.KVAR_VENTILATORA),
                "Visok RPM znaci da ventilator radi - ne sme biti KVAR_VENTILATORA");
    }

    @Test
    void nivo2_visoka_temp_chipseta_kreira_pregrevanje_chipseta() {
        session.insert(new KomponentaFakt(TipKomponente.MOTHERBOARD));
        session.insert(merenje(m -> m.setTemperaturaChipseta(88)));
        fire();

        List<TipKvara> kvarovi = wm(KvarFakt.class).stream()
                .map(KvarFakt::getTipKvara).collect(Collectors.toList());
        assertTrue(kvarovi.contains(TipKvara.PREGREVANJE_CHIPSETA));
    }

    
    // Nivo 3 - dodatni testovi
    

    @Test
    void nivo3_psu_root_cause_dobija_posebnu_poruku_uz_drugi_kvar() {
        // salience 10: NESTABILAN_NAPON + bilo koji drugi kvar -> "KOREN UZROK" poruka
        KomponentaFakt psu = new KomponentaFakt(TipKomponente.PSU);
        KomponentaFakt gpu = new KomponentaFakt(TipKomponente.GPU);
        session.insert(new KvarFakt(TipKvara.NESTABILAN_NAPON, psu));
        session.insert(new KvarFakt(TipKvara.PREGREVANJE_GPU, gpu));
        session.insert(merenje(m -> m.setTemperaturaGPU(98)));
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        DijagnozaFakt psuDijagnoza = dijagnoze.stream()
                .filter(d -> d.getKvar().getTipKvara() == TipKvara.NESTABILAN_NAPON)
                .findFirst().orElseThrow();
        assertEquals(Ozbiljnost.KRITICNO, psuDijagnoza.getOzbiljnost());
        assertTrue(psuDijagnoza.getPreporuka().contains("KOREN UZROK"),
                "PSU root cause pravilo treba da doda 'KOREN UZROK' u preporuku");
    }

    @Test
    void nivo3_eskalacija_termalna_pasta_uz_cep1_je_kriticno() {
        // Bez CEP1: ISTROSENA_TERMALNA_PASTA je UPOZORENJE (template)
        // Sa CEP1: eskalira na KRITICNO (manual pravilo, salience 5)
        KomponentaFakt k = new KomponentaFakt(TipKomponente.COOLING);
        session.insert(new KvarFakt(TipKvara.ISTROSENA_TERMALNA_PASTA, k));
        session.insert(new CepAlarmFakt("CEP1_CPU_TEMP", "ponavljajuce pregrevanje"));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.KRITICNO, dijagnoze.get(0).getOzbiljnost());
    }

    @Test
    void nivo3_eskalacija_kvar_ventilatora_uz_cep5_pominje_lezaj() {
        // Sa CEP5 (progresivni pad RPM) -> preporuka treba da pomene istroseni lezaj
        KomponentaFakt k = new KomponentaFakt(TipKomponente.COOLING);
        session.insert(new KvarFakt(TipKvara.KVAR_VENTILATORA, k));
        session.insert(new CepAlarmFakt("CEP5_RPM_PAD", "progresivni pad RPM"));
        session.insert(normalMerenje());
        fire();

        List<DijagnozaFakt> dijagnoze = wm(DijagnozaFakt.class);
        assertEquals(1, dijagnoze.size());
        assertEquals(Ozbiljnost.KRITICNO, dijagnoze.get(0).getOzbiljnost());
        assertTrue(dijagnoze.get(0).getPreporuka().contains("lezaj"),
                "Eskalacija CEP5 treba da pomene istroseni lezaj ventilatora");
    }
}
