package org.example.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TemporaryPoint extends Point {
    private double courseAngle;

    public TemporaryPoint(Double latitude, Double longitude, long flightHeight, long flightSpeed, double courseAngle) {
        super(latitude, longitude, flightHeight, flightSpeed);
        this.courseAngle = courseAngle;
    }
}
