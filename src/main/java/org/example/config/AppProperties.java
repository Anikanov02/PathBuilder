package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppProperties {
    @Value("${path-builder.run-delay-ms}")
    private long runDelayMs;

    @Value("${path-builder.time-interval-ms}")
    private long monitoringIntervalMs;
}
