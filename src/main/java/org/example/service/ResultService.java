package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.RouteCalculatorResult;
import org.example.repository.ResultsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Commonly it's better to build a service layer over
 * repositories because additional actions are required before interacting with db
 * for example, converting dto<-->model, some data manipulations, additional data validation, etc.
 * but in this case this service is basically redundant as all operations are simple
 * added for architectural sake
 */
@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultsRepository repository;

    public RouteCalculatorResult saveResult(RouteCalculatorResult result) {
        return repository.save(result);
    }

    public  RouteCalculatorResult getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public List<RouteCalculatorResult> getAllResults() {
        return repository.findAll();
    }

    public RouteCalculatorResult getForAircraft(String aircraftId) {
        return repository.findByAircraftId(aircraftId);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
