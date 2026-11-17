package com.campusconnect.dto;

import java.time.Instant;

public record DepartmentResponse(
        Long id,
        String code,
        String name,
        String description,
        int studentCount,
        int facultyCount,
        int courseCount,
        Instant createdAt
) {
}
