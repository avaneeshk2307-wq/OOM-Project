package com.campusconnect.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRegisterRequest(
        @NotNull(message = "Course ID is required")
        Long courseId,

        Integer semester
) {
}
