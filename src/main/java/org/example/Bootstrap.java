package org.example;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Airplane;
import org.example.domain.model.AirplaneCharacteristics;
import org.example.domain.model.WayPoint;
import org.example.service.AircraftFlightService;
import org.example.service.AircraftService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Bootstrap {
    private final AircraftService service;
    private final AircraftFlightService flightService;

    @EventListener(ApplicationReadyEvent.class)
    public void runScheduler() {
        flightService.schedule(getExistingAirplanesOrCreateNew(), testRoute());
    }

    private List<Airplane> getExistingAirplanesOrCreateNew() {
        final List<Airplane> planes = service.getAllAircrafts();
        //adding testcase if db is empty
        if(planes.isEmpty()) {
            final AirplaneCharacteristics characteristics0 = new AirplaneCharacteristics(400L, 9d, 15d, 2d);
            Airplane airplane = new Airplane("0", characteristics0, null, new ArrayList<>());
            planes.add(airplane);
            service.newAircraft(airplane);

            final AirplaneCharacteristics characteristics1 = new AirplaneCharacteristics(250L, 7d, 7d, 2d);
            airplane = new Airplane("1", characteristics1, null, new ArrayList<>());
            planes.add(airplane);
            service.newAircraft(airplane);

            final AirplaneCharacteristics characteristics2 = new AirplaneCharacteristics(300L, 10d, 10d, 4d);
            airplane = new Airplane("2", characteristics2, null, new ArrayList<>());
            planes.add(airplane);
            service.newAircraft(airplane);
        }

        return planes;
    }

    private List<WayPoint> testRoute() {
        return List.of(new WayPoint(50.1d, 50.1d, 2000L, 150L),
                new WayPoint(50.1d, 50.2d, 3500L, 200L),
                new WayPoint(50.2d, 50.1d, 1000L, 250L),
                new WayPoint(50.2d, 50.2d, 2000L, 175L));
    }
}
