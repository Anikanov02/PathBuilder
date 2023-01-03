package org.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private Double latitude;
    private Double longitude;
    private Long flightHeight;
    private Long flightSpeed;
}
