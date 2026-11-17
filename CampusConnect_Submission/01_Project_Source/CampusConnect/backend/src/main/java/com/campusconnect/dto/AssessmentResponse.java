package com.campusconnect.dto;

import com.campusconnect.enums.AssessmentType;
import java.math.BigDecimal;
import java.time.Instant;

public record AssessmentResponse(
        Long id,
        Long courseId,
        String courseCode,
        String name,
        AssessmentType type,
        BigDecimal maxMarks,
        BigDecimal weightagePercentage,
        Instant createdAt
) {}
