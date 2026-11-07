package com.campusconnect.dto;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public record AttendanceSessionResponse(
        Long id,
        Long courseId,
        String courseCode,
        String courseTitle,
        LocalDate sessionDate,
        String topic,
        int totalRecords,
        long presentCount,
        Instant createdAt,
        List<AttendanceRecordResponse> records
) {}
