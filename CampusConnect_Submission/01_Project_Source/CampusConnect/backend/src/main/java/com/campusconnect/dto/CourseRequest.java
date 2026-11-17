package com.campusconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "Course code is required")
        @Size(min = 2, max = 30, message = "Course code must be between 2 and 30 characters")
        String code,

        @NotBlank(message = "Course title is required")
        @Size(max = 200, message = "Course title must not exceed 200 characters")
        String title,

        String description,

        @Min(value = 1, message = "Credits must be at least 1")
        int credits,

        @Min(value = 1, message = "Semester must be at least 1")
        int semester,

        @Min(value = 1, message = "Capacity must be at least 1")
        int capacity,

        @NotNull(message = "Department ID is required")
        Long departmentId,

        Long instructorId
) {
}
