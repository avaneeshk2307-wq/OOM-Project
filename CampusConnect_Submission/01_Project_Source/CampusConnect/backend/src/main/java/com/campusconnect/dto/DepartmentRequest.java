package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank(message = "Department code is required")
        @Size(min = 2, max = 20, message = "Code must be between 2 and 20 characters")
        String code,

        @NotBlank(message = "Department name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        String description
) {
}
