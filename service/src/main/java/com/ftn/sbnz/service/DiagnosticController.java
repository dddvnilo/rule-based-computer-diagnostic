package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import com.ftn.sbnz.model.SimptomFakt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dijagnoza")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping
    public List<DijagnozaFakt> dijagnostikuj(@RequestBody SimptomFakt simptom) {
        return diagnosticService.dijagnostikuj(simptom);
    }
}
