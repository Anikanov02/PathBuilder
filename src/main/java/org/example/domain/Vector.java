package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.domain.model.Point;

@Data
@AllArgsConstructor
public class Vector {
    private Double x;
    private Double y;
    private Long z;
    private Point start;
    private Point end;

    public Vector(Point start, Point end) {
        this.start = start;
        this.end = end;

        this.x = end.getLatitude() - start.getLatitude();
        this.y = end.getLongitude() - start.getLongitude();
        this.z = end.getFlightHeight() - start.getFlightHeight();
    }

    public Vector getScaledVector(double coefficient) {
        final Double newX = x * coefficient;
        final Double newY = y * coefficient;
        final Long newZ = ((Double) (z * coefficient)).longValue();
        final Point newEnd = new Point(start.getLatitude() + newX, start.getLongitude() + newY, start.getFlightHeight() + newZ, null);
        return new Vector(start, newEnd);
    }

    public long getLengthMeters() {
        return (long) Math.sqrt(Math.pow(toMeters(x), 2) + Math.pow(toMeters(y), 2) + Math.pow(z, 2));
    }

    public double getScalarProduct(Vector other) {
        return toMeters(x) * toMeters(other.x) + toMeters(y) * toMeters(other.y) + z * other.z;
    }

    public double getAngle(Vector other) {
        return Math.acos(getScalarProduct(other) / (getLengthMeters() * other.getLengthMeters()));
    }

    public double getFlatScalarProduct(Vector other) {
        return toMeters(x) * toMeters(other.x) + toMeters(y) * toMeters(other.y);
    }

    public double getFlatAngle(Vector other) {
        final Vector flatBase = new Vector(new Point(start.getLatitude(), start.getLongitude(), 0L, 0L),
                new Point(end.getLatitude(), end.getLongitude(), 0L, 0L));
        final Vector flatOther = new Vector(new Point(other.start.getLatitude(), other.start.getLongitude(), 0L, 0L),
                new Point(other.end.getLatitude(), other.end.getLongitude(), 0L, 0L));
        return Math.acos(flatBase.getFlatScalarProduct(flatOther) / (flatBase.getLengthMeters() * flatOther.getLengthMeters()));
    }

    private double toMeters(double coordinate) {
        return Math.abs(coordinate) * 111139;
    }
}
