package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AppProperties;
import org.example.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class AircraftFlightService {
    private final AppProperties properties;
    private final RouteCalculator routeCalculator;
    private final ResultService resultService;
    private final AircraftService aircraftService;

    /**
     * Schedule flights of aircrafts on a certain route
     */
    public void schedule(List<Airplane> airplanes, List<WayPoint> wayPoints) {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(airplanes.size());
        final AtomicLong flightDelay = new AtomicLong(0);
        final List<Runnable> flights = airplanes.stream().map(airplane -> {
            final List<TemporaryPoint> detailedRoute = routeCalculator.calculateRoute(airplane.getCharacteristics(), wayPoints);
            resultService.saveResult(RouteCalculatorResult.builder().aircraftId(airplane.getId()).points(detailedRoute).build());
            log.info("Result for aircraft {} saved to db", airplane.getId());
            return (Runnable)() -> run(airplane, wayPoints, detailedRoute);
        }).toList();
        flights.forEach(flight -> executorService.schedule(flight,  flightDelay.getAndAdd(properties.getRunDelayMs()), TimeUnit.MILLISECONDS));
    }

    /**
     * Start a flight of an aircraft by a certain route
     * Firstly we calculate route, then update data every fixed amount of time
     */
    public void run(Airplane airplane, List<WayPoint> wayPoints, List<TemporaryPoint> detailedRoute) {
        log.info("Aircraft {} started its flight, previous flights:", airplane.getId());
        airplane.getFlights().forEach(flight -> log.info(flight.toString()));
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final AtomicLong pointPassingDelay = new AtomicLong(0);

        final Flight newFlight = Flight.builder().wayPoints(wayPoints).passedPoints(new ArrayList<>()).build();
        airplane.getFlights().add(newFlight);
        final List<Runnable> tasks = detailedRoute.stream().map(tempPoint -> (Runnable) () -> {
            newFlight.getPassedPoints().add(tempPoint);
            airplane.setPosition(tempPoint);
            aircraftService.updateAircraftData(airplane);
            log.info("Aircraft {} passed point {}", airplane.getId(), tempPoint);
        }).toList();
        tasks.forEach(task -> executorService.schedule(task, pointPassingDelay.getAndAdd(properties.getMonitoringIntervalMs()), TimeUnit.MILLISECONDS));
    }
}
