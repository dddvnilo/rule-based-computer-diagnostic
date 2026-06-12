package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import com.ftn.sbnz.model.KorisnikOdgovori;
import com.ftn.sbnz.model.MerenjeEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping("/merenja")
    public ResponseEntity<Void> primiMerenje(@RequestBody MerenjeEvent event) {
        diagnosticService.updateMerenje(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dijagnoza")
    public ResponseEntity<List<DijagnozaFakt>> dijagnostikuj(@RequestBody KorisnikOdgovori odgovori) {
        return ResponseEntity.ok(diagnosticService.dijagnostikuj(odgovori));
    }

}
