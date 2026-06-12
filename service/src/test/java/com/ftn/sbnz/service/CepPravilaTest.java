package com.ftn.sbnz.service;

import com.ftn.sbnz.model.CepAlarmFakt;
import com.ftn.sbnz.model.MerenjeEvent;
import org.junit.jupiter.api.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CepPravilaTest {

    @Autowired
    private KieContainer kieContainer;

    private KieSession session;
    private SessionPseudoClock clock;

    @BeforeEach
    void setUp() {
        KieSessionConfiguration config = KieServices.Factory.get().newKieSessionConfiguration();
        config.setOption(ClockTypeOption.get("pseudo"));
        session = kieContainer.newKieSession(config);
        clock = session.getSessionClock();
    }

    @AfterEach
    void tearDown() {
        session.dispose();
    }

    // --- Helpers ---

    private void fireCep() {
        session.fireAllRules(match -> match.getRule().getName().startsWith("CEP-"));
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

    private MerenjeEvent merenjeTemp(double tempCPU) {
        MerenjeEvent m = normalMerenje();
        m.setTemperaturaCPU(tempCPU);
        return m;
    }

    private MerenjeEvent merenjeSmart(int reallocated, int pending, int uncorrectable) {
        MerenjeEvent m = normalMerenje();
        m.setSmartReallocatedSectors(reallocated);
        m.setSmartPendingSectors(pending);
        m.setSmartUncorrectableErrors(uncorrectable);
        return m;
    }

    private MerenjeEvent merenjePing(double ping) {
        MerenjeEvent m = normalMerenje();
        m.setPingMs(ping);
        return m;
    }

    private MerenjeEvent merenjeNapon(double napon12V) {
        MerenjeEvent m = normalMerenje();
        m.setNapon12V(napon12V);
        return m;
    }

    private MerenjeEvent merenjeRpm(int rpmCPU) {
        MerenjeEvent m = normalMerenje();
        m.setRpmCPUVentilator(rpmCPU);
        return m;
    }

    
    // CEP-1: ponavljajuće pregrevanje CPU (3+ puta u 10 minuta)
    

    @Test
    void cep1_tri_pregrevanja_u_10_minuta_okida_alarm() {
        session.insert(merenjeTemp(95));
        fireCep();

        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(93));
        fireCep();

        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(91));
        fireCep();

        List<CepAlarmFakt> alarmi = wm(CepAlarmFakt.class);
        assertEquals(1, alarmi.size());
        assertEquals("CEP1_CPU_TEMP", alarmi.get(0).getTip());
    }

    @Test
    void cep1_dva_pregrevanja_ne_okida_alarm() {
        session.insert(merenjeTemp(95));
        fireCep();

        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(93));
        fireCep();

        assertTrue(wm(CepAlarmFakt.class).isEmpty(),
                "Samo 2 pregrevanja ne sme okinu CEP1 alarm (potrebno >= 3)");
    }

    @Test
    void cep1_alarm_ne_ponavlja_se_dok_je_aktivan() {
        // Okida alarm sa 3 merenja
        session.insert(merenjeTemp(95));
        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(93));
        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(91));
        fireCep();

        assertEquals(1, wm(CepAlarmFakt.class).size());

        // Dodaj jos 3 merenja dok je alarm jos aktivan (< 1min od kreacije)
        session.insert(merenjeTemp(96));
        session.insert(merenjeTemp(94));
        session.insert(merenjeTemp(92));
        fireCep();

        // "not CepAlarmFakt(tip == "CEP1_CPU_TEMP")" blokira duplikat
        assertEquals(1, wm(CepAlarmFakt.class).size(),
                "CEP1 alarm ne sme da se duplikuje dok je aktivan");
    }

    
    // CEP-2: ucestale SMART greške diska (5+ puta u 24 sata)
    

    @Test
    void cep2_pet_smart_gresaka_u_24h_okida_alarm() {
        for (int i = 0; i < 5; i++) {
            session.insert(merenjeSmart(2, 1, 0));
            fireCep();
            if (i < 4) clock.advanceTime(4, TimeUnit.HOURS);
        }

        List<CepAlarmFakt> alarmi = wm(CepAlarmFakt.class);
        assertTrue(alarmi.stream().anyMatch(a -> a.getTip().equals("CEP2_SMART_DISK")),
                "5 SMART gresaka u 24h treba okinu CEP2 alarm");
    }

    
    // CEP-3: nestabilan ping (3+ puta > 200ms u 5 minuta)
    

    @Test
    void cep3_tri_visoka_pinga_u_5_minuta_okida_alarm() {
        session.insert(merenjePing(300));
        fireCep();

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.insert(merenjePing(250));
        fireCep();

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.insert(merenjePing(280));
        fireCep();

        List<CepAlarmFakt> alarmi = wm(CepAlarmFakt.class);
        assertTrue(alarmi.stream().anyMatch(a -> a.getTip().equals("CEP3_PING_SKOK")),
                "3 merenja sa pingom > 200ms u 5 minuta treba okinu CEP3 alarm");
    }

    
    // CEP-4: oscilacija napona 12V (raspon > 0.6V, min 5 merenja u 5 minuta)
    

    @Test
    void cep4_oscilacija_napona_okida_alarm() {
        // Naizmenicno 12.9V / 12.1V -> raspon 0.8V > 0.6V
        double[] naponi = {12.9, 12.1, 12.9, 12.1, 12.9};
        for (int i = 0; i < naponi.length; i++) {
            session.insert(merenjeNapon(naponi[i]));
            fireCep();
            if (i < naponi.length - 1) clock.advanceTime(1, TimeUnit.MINUTES);
        }

        List<CepAlarmFakt> alarmi = wm(CepAlarmFakt.class);
        assertTrue(alarmi.stream().anyMatch(a -> a.getTip().equals("CEP4_NAPON_OSCILACIJA")),
                "Oscilacija napona 0.8V u 5 merenja treba okinu CEP4 alarm");
    }

    
    // CEP-5: progresivni pad RPM CPU ventilatora (5 uzastopnih merenja)
    

    @Test
    void cep5_progresivni_pad_rpm_okida_alarm() {
        // Svako merenje je < prethodnog i unutar 2 minuta od prethodnog
        int[] rpmi = {1400, 1300, 1200, 1100, 1000};
        for (int i = 0; i < rpmi.length; i++) {
            session.insert(merenjeRpm(rpmi[i]));
            fireCep();
            if (i < rpmi.length - 1) clock.advanceTime(1, TimeUnit.MINUTES);
        }

        List<CepAlarmFakt> alarmi = wm(CepAlarmFakt.class);
        assertTrue(alarmi.stream().anyMatch(a -> a.getTip().equals("CEP5_RPM_PAD")),
                "5 uzastopnih pada RPM u 2-minutnim razmacima treba okinu CEP5 alarm");
    }

    
    // Istek vremenskog prozora - window:time izbacuje stare dogadjaje
    

    @Test
    void cep1_merenja_van_prozora_ne_okidaju_alarm() {
        // T=0: prvo merenje
        session.insert(merenjeTemp(95));
        fireCep();

        // T=5min: drugo merenje
        clock.advanceTime(5, TimeUnit.MINUTES);
        session.insert(merenjeTemp(93));
        fireCep();

        // T=11min: trece merenje - prvo (T=0) je ispalo iz window:time(10m)
        // prozor sad pokriva [1min, 11min], samo 2 merenja unutra -> alarm ne okida
        clock.advanceTime(6, TimeUnit.MINUTES);
        session.insert(merenjeTemp(91));
        fireCep();

        assertTrue(wm(CepAlarmFakt.class).isEmpty(),
                "Prvo merenje (T=0) ispalo iz 10-minutnog prozora, ostala su samo 2 -> alarm ne sme okinu");
    }

    
    // Rearm nakon @expires(1m) - alarm se ponovo okida posle cooldown-a
    

    @Test
    void cep1_alarm_se_rearma_nakon_isteka_cooldown_perioda() {
        // Prvo okidanje: 3 merenja u 10 minuta
        session.insert(merenjeTemp(95));
        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(93));
        clock.advanceTime(3, TimeUnit.MINUTES);
        session.insert(merenjeTemp(91));
        fireCep();

        assertEquals(1, wm(CepAlarmFakt.class).size(), "Prvo okidanje: 1 alarm");

        // Advance 2 minuta: alarm (@expires 1m) je istekao, ali stara merenja su i dalje u 10min prozoru
        clock.advanceTime(2, TimeUnit.MINUTES);
        fireCep();

        // Stara merenja (T=0, T=3, T=6) su i dalje u prozoru (sada je T=8min, prozor [0, 8min])
        // alarm je istekao -> not CepAlarmFakt je true -> alarm se ponovo kreira
        assertEquals(1, wm(CepAlarmFakt.class).size(),
                "Nakon cooldown-a alarm se rearma - ponovo 1 aktivan alarm");
    }

    
    // CEP-5 negativni - RPM nije strogo opadan
    

    @Test
    void cep5_rpm_nije_monoton_ne_okida_alarm() {
        // Cetvrto merenje (1150) vece od treceg (1100) -> niz nije strogo opadajuci
        int[] rpmi = {1400, 1300, 1100, 1150, 1000};
        for (int i = 0; i < rpmi.length; i++) {
            session.insert(merenjeRpm(rpmi[i]));
            fireCep();
            if (i < rpmi.length - 1) clock.advanceTime(1, TimeUnit.MINUTES);
        }

        assertFalse(wm(CepAlarmFakt.class).stream().anyMatch(a -> a.getTip().equals("CEP5_RPM_PAD")),
                "RPM koji raste u jednom koraku ne sme okinu CEP5 alarm");
    }

    
    // CEP-4 negativni - oscilacija ispod praga
    

    @Test
    void cep4_mala_oscilacija_ne_okida_alarm() {
        // Raspon: 12.35 - 12.1 = 0.25V < 0.6V -> alarm ne okida
        double[] naponi = {12.35, 12.1, 12.3, 12.15, 12.25};
        for (int i = 0; i < naponi.length; i++) {
            session.insert(merenjeNapon(naponi[i]));
            fireCep();
            if (i < naponi.length - 1) clock.advanceTime(1, TimeUnit.MINUTES);
        }

        assertFalse(wm(CepAlarmFakt.class).stream().anyMatch(a -> a.getTip().equals("CEP4_NAPON_OSCILACIJA")),
                "Oscilacija od 0.25V (< 0.6V prag) ne sme okinu CEP4 alarm");
    }

    
    // CEP-2 negativni - nedovoljno SMART gresaka
    

    @Test
    void cep2_cetiri_smart_greske_ne_okidaju_alarm() {
        for (int i = 0; i < 4; i++) {
            session.insert(merenjeSmart(2, 1, 0));
            fireCep();
            if (i < 3) clock.advanceTime(4, TimeUnit.HOURS);
        }

        assertFalse(wm(CepAlarmFakt.class).stream().anyMatch(a -> a.getTip().equals("CEP2_SMART_DISK")),
                "4 SMART greske (< 5) ne smeju okinu CEP2 alarm");
    }
}
