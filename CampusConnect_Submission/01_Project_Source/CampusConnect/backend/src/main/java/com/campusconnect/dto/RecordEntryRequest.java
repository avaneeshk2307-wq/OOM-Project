package com.campusconnect.dto;

import com.campusconnect.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record RecordEntryRequest(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {}
