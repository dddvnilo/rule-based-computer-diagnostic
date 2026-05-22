package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import com.ftn.sbnz.model.SimptomFakt;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticService {

    private final KieContainer kieContainer;

    public DiagnosticService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public List<DijagnozaFakt> dijagnostikuj(SimptomFakt simptom) {
        System.out.println("DEBUG: KieBases dostupne: " + kieContainer.getKieBaseNames());
        KieSession session = kieContainer.newKieSession("DiagnosticsKSession");
        System.out.println("DEBUG: Sesija kreirana: " + (session != null));
        try {
            session.insert(simptom);
            int fired = session.fireAllRules();
            System.out.println("DEBUG: Pravila koja su se aktivirala: " + fired);
            System.out.println("DEBUG: Ukupno objekata u sesiji: " + session.getObjects().size());

            List<DijagnozaFakt> rezultati = new ArrayList<>();
            session.getObjects(obj -> obj instanceof DijagnozaFakt)
                   .forEach(obj -> rezultati.add((DijagnozaFakt) obj));
            return rezultati;
        } finally {
            session.dispose();
        }
    }
}
