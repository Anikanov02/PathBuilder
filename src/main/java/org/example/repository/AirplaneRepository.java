package org.example.repository;

import org.example.domain.model.Airplane;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirplaneRepository extends MongoRepository<Airplane, String> {
}
