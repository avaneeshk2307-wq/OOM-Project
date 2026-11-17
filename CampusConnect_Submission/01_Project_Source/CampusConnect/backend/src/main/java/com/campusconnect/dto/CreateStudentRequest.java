package com.campusconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email is required")
        String email,

        String phone,

        @NotBlank(message = "Roll number is required")
        @Size(min = 2, max = 50, message = "Roll number must be between 2 and 50 characters")
        String rollNumber,

        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 12, message = "Semester cannot exceed 12")
        int semester,

        @NotNull(message = "Department ID is required")
        Long departmentId,

        @NotBlank(message = "Initial password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String initialPassword
) {
}
