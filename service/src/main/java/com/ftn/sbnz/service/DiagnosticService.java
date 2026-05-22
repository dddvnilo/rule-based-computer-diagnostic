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
        KieSession session = kieContainer.newKieSession("DiagnosticsKSession");
        try {
            session.insert(simptom);
            session.fireAllRules();

            List<DijagnozaFakt> rezultati = new ArrayList<>();
            session.getObjects(obj -> obj instanceof DijagnozaFakt)
                   .forEach(obj -> rezultati.add((DijagnozaFakt) obj));
            return rezultati;
        } finally {
            session.dispose();
        }
    }
}
