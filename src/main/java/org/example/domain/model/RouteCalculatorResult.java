package org.example.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
public class RouteCalculatorResult {
    @Id
    private String id;
    private String aircraftId;
    private List<TemporaryPoint> points;
}
