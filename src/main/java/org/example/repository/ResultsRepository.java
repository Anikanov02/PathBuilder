package org.example.repository;

import org.example.domain.model.RouteCalculatorResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResultsRepository extends MongoRepository<RouteCalculatorResult, String> {
    RouteCalculatorResult findByAircraftId(String aircraftId);
}
