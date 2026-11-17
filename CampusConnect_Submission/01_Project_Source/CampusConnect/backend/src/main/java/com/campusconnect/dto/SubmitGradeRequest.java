package com.campusconnect.dto;

import com.campusconnect.enums.GradingMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SubmitGradeRequest(
        @NotNull(message = "Enrollment ID is required")
        Long enrollmentId,

        @NotNull(message = "Assessment ID is required")
        Long assessmentId,

        @NotNull(message = "Marks obtained is required")
        @DecimalMin(value = "0.00", message = "Marks obtained cannot be negative")
        BigDecimal marksObtained,

        GradingMode gradingMode
) {
    public SubmitGradeRequest(Long enrollmentId, Long assessmentId, BigDecimal marksObtained) {
        this(enrollmentId, assessmentId, marksObtained, GradingMode.ABSOLUTE);
    }
}
