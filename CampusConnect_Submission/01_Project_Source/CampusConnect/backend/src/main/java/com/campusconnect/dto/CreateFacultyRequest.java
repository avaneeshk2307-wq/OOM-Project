package com.campusconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFacultyRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email is required")
        String email,

        String phone,

        @NotBlank(message = "Employee code is required")
        @Size(min = 2, max = 50, message = "Employee code must be between 2 and 50 characters")
        String employeeCode,

        @NotBlank(message = "Designation is required")
        String designation,

        @NotNull(message = "Department ID is required")
        Long departmentId,

        @NotBlank(message = "Initial password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String initialPassword
) {
}
