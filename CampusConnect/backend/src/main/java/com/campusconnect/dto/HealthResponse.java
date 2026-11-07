package com.campusconnect.dto;

import java.time.Instant;

/**
 * HealthResponse - Immutable record representing system health and architectural metadata.
 */
public record HealthResponse(
        String status,
        String service,
        String version,
        Instant timestamp,
        String architecture
) {
    public static HealthResponse ok() {
        return new HealthResponse(
                "UP",
                "CampusConnect Backend",
                "1.0.0",
                Instant.now(),
                "Layered Modular Monolithic Architecture (OOM)"
        );
    }
}
