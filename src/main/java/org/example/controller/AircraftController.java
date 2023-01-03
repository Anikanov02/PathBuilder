package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Airplane;
import org.example.service.AircraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pathBuilder")
public class AircraftController {
    private final AircraftService airctaftService;

    @GetMapping("/all")
    public ResponseEntity<List<Airplane>> getAircrafts() {
        return ResponseEntity.ok(airctaftService.getAllAircrafts());
    }

    @GetMapping("/{aircraftId}")
    public ResponseEntity<Airplane> getAirctaftById(@PathVariable String aircraftId) {
        return ResponseEntity.ok(airctaftService.getById(aircraftId));
    }
}
