package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import com.ftn.sbnz.model.SimptomFakt;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DiagnozaService {

    @Autowired
    private KieContainer kieContainer;

    public List<DijagnozaFakt> dijagnostikuj(DiagnozaRequest request) {
        KieSession kieSession = kieContainer.newKieSession();

        // Kreiramo SimptomFakt iz request-a
        SimptomFakt simptom = new SimptomFakt();
        simptom.setTemperaturaCPU(request.getTemperaturaCPU());
        simptom.setTemperaturaGPU(request.getTemperaturaGPU());
        simptom.setRpmVentilator(request.getRpmVentilator());
        simptom.setMemtestGreske(request.getMemtestGreske());
        simptom.setNapon12V(request.getNapon12V());
        simptom.setPacketLoss(request.getPacketLoss());
        simptom.setArtefaktiNaEkranu(request.isArtefaktiNaEkranu());
        simptom.setBsod(request.isBsod());
        simptom.setBsodKod(request.getBsodKod());
        simptom.setNeobicniZvukovi(request.isNeobicniZvukovi());
        simptom.setZamrzavanje(request.isZamrzavanje());

        // Ubacujemo fakt u sesiju i pokrećemo pravila
        kieSession.insert(simptom);
        kieSession.fireAllRules();

        // Prikupljamo sve DijagnozaFakt objekte
        Collection<?> fakti = kieSession.getObjects();
        List<DijagnozaFakt> dijagnoze = new ArrayList<>();
        for (Object fakt : fakti) {
            if (fakt instanceof DijagnozaFakt) {
                dijagnoze.add((DijagnozaFakt) fakt);
            }
        }

        kieSession.dispose();
        return dijagnoze;
    }
}
