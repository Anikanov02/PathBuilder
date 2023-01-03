package org.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirplaneCharacteristics {
    private Long maxSpeed;
    private Double acceleration;
    private Double heightChangeSpeed;
    private Double courseChangeSpeed;
}
