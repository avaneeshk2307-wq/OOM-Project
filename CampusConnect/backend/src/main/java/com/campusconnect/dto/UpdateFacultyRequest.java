package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateFacultyRequest(
        @NotBlank(message = "Name is required")
        String name,

        String phone,

        @NotBlank(message = "Designation is required")
        String designation,

        @NotNull(message = "Department ID is required")
        Long departmentId
) {
}
