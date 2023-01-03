package org.example.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WayPoint extends Point {
    public WayPoint(Double latitude, Double longitude, Long flightHeight, Long flightSpeed) {
        super(latitude, longitude, flightHeight, flightSpeed);
    }
}
