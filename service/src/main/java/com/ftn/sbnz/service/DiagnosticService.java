package com.ftn.sbnz.service;

import com.ftn.sbnz.model.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ClassObjectFilter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiagnosticService {

    private final KieSession cepKieSession;
    private final KieContainer kieContainer;
    private final SimpMessagingTemplate messagingTemplate;

    private volatile MerenjeEvent latestMerenje;

    public DiagnosticService(KieSession cepKieSession, KieContainer kieContainer,
                             SimpMessagingTemplate messagingTemplate) {
        this.cepKieSession = cepKieSession;
        this.kieContainer = kieContainer;
        this.messagingTemplate = messagingTemplate;
    }

    // --- CEP: kontinualni monitoring ---

    public synchronized void updateMerenje(MerenjeEvent event) {
        latestMerenje = event;

        Set<String> prethodniAlarmi = cepKieSession.getObjects(new ClassObjectFilter(CepAlarmFakt.class))
                .stream().map(o -> ((CepAlarmFakt) o).getTip()).collect(Collectors.toSet());

        cepKieSession.insert(event);
        cepKieSession.fireAllRules(match -> match.getRule().getName().startsWith("CEP-"));

        cepKieSession.getObjects(new ClassObjectFilter(CepAlarmFakt.class))
                .stream()
                .map(o -> (CepAlarmFakt) o)
                .filter(a -> !prethodniAlarmi.contains(a.getTip()))
                .forEach(alarm -> messagingTemplate.convertAndSend("/topic/alarmi", alarm));
    }

    // --- Dijagnoza: request-based, izolована sesija ---

    public synchronized List<DijagnozaFakt> dijagnostikuj(KorisnikOdgovori odgovori) {
        KieSession session = kieContainer.newKieSession();
        try {
            if (latestMerenje != null) {
                session.insert(latestMerenje);
            }
            session.insert(odgovori);
            cepKieSession.getObjects(new ClassObjectFilter(CepAlarmFakt.class))
                    .forEach(session::insert);
            session.fireAllRules(match -> !match.getRule().getName().startsWith("CEP-"));

            List<DijagnozaFakt> dijagnoze = new ArrayList<>();
            session.getObjects(new ClassObjectFilter(DijagnozaFakt.class))
                    .forEach(obj -> dijagnoze.add((DijagnozaFakt) obj));
            return dijagnoze;
        } finally {
            session.dispose();
        }
    }
}
