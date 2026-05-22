package com.ftn.sbnz.service;

import com.ftn.sbnz.model.DijagnozaFakt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dijagnoza")
public class DiagnozaController {

    @Autowired
    private DiagnozaService diagnozaService;

    @PostMapping
    public ResponseEntity<List<DijagnozaFakt>> dijagnostikuj(@RequestBody DiagnozaRequest request) {
        List<DijagnozaFakt> rezultat = diagnozaService.dijagnostikuj(request);
        return ResponseEntity.ok(rezultat);
    }
}
