package com.campusconnect.dto;

import com.campusconnect.enums.EnrollmentStatus;
import java.time.Instant;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        String rollNumber,
        Long courseId,
        String courseCode,
        String courseTitle,
        int credits,
        int semester,
        EnrollmentStatus status,
        Instant registeredAt
) {
}
