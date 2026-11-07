package com.campusconnect.dto;

import com.campusconnect.enums.AssessmentType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAssessmentRequest(
        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Assessment name is required")
        String name,

        @NotNull(message = "Assessment type is required")
        AssessmentType type,

        @NotNull(message = "Max marks is required")
        @DecimalMin(value = "0.01", message = "Max marks must be greater than 0")
        BigDecimal maxMarks,

        @NotNull(message = "Weightage percentage is required")
        @DecimalMin(value = "0.01", message = "Weightage must be greater than 0")
        @DecimalMax(value = "100.00", message = "Weightage cannot exceed 100%")
        BigDecimal weightagePercentage
) {}
