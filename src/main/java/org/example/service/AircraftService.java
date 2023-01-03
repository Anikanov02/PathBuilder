package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Airplane;
import org.example.repository.AirplaneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftService {
    private final AirplaneRepository repository;

    public Airplane newAircraft(Airplane result) {
        return repository.save(result);
    }

    public Airplane getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Airplane> getAllAircrafts() {
        return repository.findAll();
    }

    public Airplane updateAircraftData(Airplane aircraft) {
        repository.delete(aircraft);
        return repository.save(aircraft);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
