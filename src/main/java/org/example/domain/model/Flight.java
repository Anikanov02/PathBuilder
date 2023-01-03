package org.example.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Flight {
    //TODO generated?
    private Long number;
    private List<WayPoint> wayPoints;
    private List<TemporaryPoint> passedPoints;
}
