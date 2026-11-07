package com.campusconnect.dto;

import java.time.Instant;

public record CourseResponse(
        Long id,
        String code,
        String title,
        String description,
        int credits,
        int semester,
        int capacity,
        long enrolledCount,
        Long departmentId,
        String departmentName,
        Long instructorId,
        String instructorName,
        Instant createdAt
) {
}
