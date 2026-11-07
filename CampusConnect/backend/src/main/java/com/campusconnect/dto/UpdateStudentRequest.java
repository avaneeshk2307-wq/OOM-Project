package com.campusconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStudentRequest(
        @NotBlank(message = "Name is required")
        String name,

        String phone,

        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 12, message = "Semester cannot exceed 12")
        int semester,

        @NotNull(message = "Department ID is required")
        Long departmentId
) {
}
