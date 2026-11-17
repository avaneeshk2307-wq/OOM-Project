package com.campusconnect.dto;

import com.campusconnect.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClassroomRequest(
        @NotBlank(message = "Room number is required")
        String roomNumber,

        @NotBlank(message = "Building name is required")
        String building,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotNull(message = "Room type is required")
        RoomType roomType
) {}

