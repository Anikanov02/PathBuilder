package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.RouteCalculatorResult;
import org.example.service.ResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/results")
public class ResultController {
    private final ResultService resultService;

    @GetMapping("/all")
    public ResponseEntity<List<RouteCalculatorResult>> getResults() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<RouteCalculatorResult> getResultById(@PathVariable String resultId) {
        return ResponseEntity.ok(resultService.getById(resultId));
    }

    @GetMapping("/for/{aircraftId}")
    public ResponseEntity<RouteCalculatorResult> getResultsByAircraftId(@PathVariable String aircraftId) {
        return ResponseEntity.ok(resultService.getForAircraft(aircraftId));
    }
}
