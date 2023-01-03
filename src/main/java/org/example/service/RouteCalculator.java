package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.AppProperties;
import org.example.domain.Vector;
import org.example.domain.model.AirplaneCharacteristics;
import org.example.domain.model.Point;
import org.example.domain.model.TemporaryPoint;
import org.example.domain.model.WayPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteCalculator {
    private final AppProperties properties;

    /**
     * calculates the whole route
     */
    public List<TemporaryPoint> calculateRoute(AirplaneCharacteristics characteristics, List<WayPoint> wayPoints) {
        final Iterator<WayPoint> iterator = wayPoints.iterator();
        final List<TemporaryPoint> result = new ArrayList<>();
        WayPoint curr = iterator.next();
        while (iterator.hasNext()) {
            final WayPoint next = iterator.next();
            result.addAll(getTempPoints(characteristics, new Vector(curr, next)));
            curr = next;
        }
        return result;
    }

    /**
     * calculates all temp points from one way point to another
     */
    private List<TemporaryPoint> getTempPoints(AirplaneCharacteristics characteristics, Vector vector) {
        final List<TemporaryPoint> points = new ArrayList<>();
        final List<Point> criticalPoints = getCriticalPoints(characteristics, vector);
        final Iterator<Point> iterator = criticalPoints.iterator();
        Point curr = iterator.next();
        while (iterator.hasNext()) {
            final Point next = iterator.next();
            points.addAll(getRoutePartition(characteristics, new Vector(curr, next)));
            curr = next;
        }
        return points;
    }

    private List<TemporaryPoint> getRoutePartition(AirplaneCharacteristics characteristics, Vector vector) {
        final List<TemporaryPoint> points = new ArrayList<>();
        double accumulatedDistance = 0;
        long currentSpeed = vector.getStart().getFlightSpeed();
        double acceleration = vector.getStart().getFlightSpeed().equals(vector.getEnd().getFlightSpeed()) ? 0L :
                vector.getStart().getFlightSpeed().compareTo(vector.getEnd().getFlightSpeed()) < 0 ? characteristics.getAcceleration() : -characteristics.getAcceleration();
        while (accumulatedDistance <= vector.getLengthMeters()) {
            //if speed change would exceed our limits on next step - then set acc to 0 and speed to required() this speed would be reached
            //somewhere between
            if((Math.abs(vector.getStart().getFlightSpeed() - vector.getEnd().getFlightSpeed()) * properties.getMonitoringIntervalMs() / 1000d) < Math.abs(acceleration)) {
                acceleration = 0;
                currentSpeed = vector.getEnd().getFlightSpeed();
            }
            accumulatedDistance += S(currentSpeed, properties.getMonitoringIntervalMs() / 1000, acceleration);
            if (accumulatedDistance <= vector.getLengthMeters()) {
                double coefficient = accumulatedDistance / vector.getLengthMeters();
                final Vector part = vector.getScaledVector(coefficient);
                currentSpeed = V(currentSpeed, properties.getMonitoringIntervalMs() / 1000, acceleration);
                part.getEnd().setFlightSpeed(currentSpeed);
                final Point end = part.getEnd();
                points.add(new TemporaryPoint(end.getLatitude(), end.getLongitude(), end.getFlightHeight(), end.getFlightSpeed(), getCourse(part)));
            }
        }
        return points;
    }

    /**
     * we split way from 1 way point to another on N conditional
     * parts. Each part describes how an aircraft will move
     * e.g.
     * 1 - accelerated movement from 300 ms to 400ms kmh
     * 2 - even movement 400ms kmh
     * 3 - accelerated movement from 400ms to 200ms kmh
     * in our case there will be ether 1, 2 or 3 parts as we do not consider
     * cases where there can be over 2 accelerated parts on the way
     */
    private List<Point> getCriticalPoints(AirplaneCharacteristics characteristics, Vector vector) {
        final List<Point> criticalPoints = new ArrayList<>();
        criticalPoints.add(vector.getStart());

        long accelerationTime = (long) ((characteristics.getMaxSpeed() - vector.getStart().getFlightSpeed()) / characteristics.getAcceleration());
        long breakingTime = (long) Math.abs((characteristics.getMaxSpeed() - vector.getEnd().getFlightSpeed()) / characteristics.getAcceleration());
        double accelerationRoute = S(vector.getStart().getFlightSpeed(), accelerationTime, characteristics.getAcceleration());
        double breakingRoute = S(vector.getEnd().getFlightSpeed(), breakingTime, characteristics.getAcceleration());

        if (vector.getLengthMeters() <= accelerationRoute + breakingRoute) {
            final boolean accelerating = vector.getStart().getFlightSpeed() < vector.getEnd().getFlightSpeed();

            if(accelerating) {
                accelerationTime = (long) ((vector.getEnd().getFlightSpeed() - vector.getStart().getFlightSpeed()) / characteristics.getAcceleration());
                accelerationRoute = S(vector.getStart().getFlightSpeed(), accelerationTime, characteristics.getAcceleration());
                if(accelerationRoute > vector.getLengthMeters()) {
                    throw  new RuntimeException("Unreachable speed at waypoint");
                }
                double coefficient = accelerationRoute / vector.getLengthMeters();
                final Vector accelerationPart = vector.getScaledVector(coefficient);
                accelerationPart.getEnd().setFlightSpeed(V(accelerationPart.getStart().getFlightSpeed(), accelerationTime, characteristics.getAcceleration()));
                criticalPoints.add(accelerationPart.getEnd());
            } else {
                breakingTime = (long) ((vector.getStart().getFlightSpeed() - vector.getEnd().getFlightSpeed()) / characteristics.getAcceleration());
                breakingRoute = S(vector.getEnd().getFlightSpeed(), breakingTime, characteristics.getAcceleration());
                if(breakingRoute > vector.getLengthMeters()) {
                    throw  new RuntimeException("Unreachable speed at waypoint");
                }
                double coefficient = breakingRoute / vector.getLengthMeters();
                final Vector breakingPart = vector.getScaledVector(coefficient);
                breakingPart.getEnd().setFlightSpeed(V(breakingPart.getStart().getFlightSpeed(), breakingTime, -characteristics.getAcceleration()));
                criticalPoints.add(breakingPart.getEnd());
            }
        } else {
            double coefficient = accelerationRoute / vector.getLengthMeters();
            final Vector accelerationPart = vector.getScaledVector(coefficient);
            accelerationPart.getEnd().setFlightSpeed(V(accelerationPart.getStart().getFlightSpeed(), accelerationTime, characteristics.getAcceleration()));
            criticalPoints.add(accelerationPart.getEnd());

            coefficient = 1 - (breakingRoute / vector.getLengthMeters());
            //whole route without breaking part (the end point will be a start of breaking part)
            final Vector part = vector.getScaledVector(coefficient);
            part.getEnd().setFlightSpeed(characteristics.getMaxSpeed());
            criticalPoints.add(part.getEnd());
        }
        criticalPoints.add(vector.getEnd());
        return criticalPoints;
    }

    private double getCourse(Vector vector) {
        final Vector northDir = new Vector(vector.getStart(), new Point(90d, vector.getStart().getLongitude(), 0L, 0L));
        return vector.getFlatAngle(northDir);
    }

    //S = V0t + at^2/2
    private double S(float V0, long t, double a) {
        return V0 * t + a * Math.pow(t, 2) / 2;
    }

    //V = V0 + at
    private long V(double V0, long t, double a) {
        return (long) (V0 + a * t);
    }

    private long T(double V0, double S, double a) {
        //square equation
        long x1 = (long) (-V0 + Math.sqrt(D(a/2, V0, - S)));
        long x2 = (long) (-V0 - Math.sqrt(D(a/2, V0, - S)));
        if(x1 > 0) {
            return x1;
        } else if (x2 > 0) {
            return x2;
        } else {
            throw new RuntimeException("No real solution of square equation");
        }
    }

    private double D(double a, double b, double c) {
        return Math.pow(b, 2) - 4 * a * c;
    }
}