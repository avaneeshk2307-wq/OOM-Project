package com.campusconnect.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CreateSessionRequest(
        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotNull(message = "Session date is required")
        LocalDate sessionDate,

        String topic,

        List<RecordEntryRequest> records
) {}
