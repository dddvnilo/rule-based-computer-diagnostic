package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import com.ftn.sbnz.model.KorisnikOdgovori;
import com.ftn.sbnz.model.MerenjeEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticService {

    private final KieContainer kieContainer;
    private final MerenjeStore merenjeStore;

    public DiagnosticService(KieContainer kieContainer, MerenjeStore merenjeStore) {
        this.kieContainer = kieContainer;
        this.merenjeStore = merenjeStore;
    }

    public void updateMerenje(MerenjeEvent event) {
        merenjeStore.setLatest(event);
    }

    public List<DijagnozaFakt> dijagnostikuj(KorisnikOdgovori odgovori) {
        if (!merenjeStore.hasData()) {
            throw new IllegalStateException("Nema izmerenih vrednosti. Simulator jos nije poslao merenje.");
        }

        KieSession session = kieContainer.newKieSession("DiagnosticsKSession");
        try {
            session.insert(merenjeStore.getLatest());
            session.insert(odgovori);
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
