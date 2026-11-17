package com.campusconnect.dto;

import com.campusconnect.enums.AttendanceStatus;
import java.time.LocalDate;

public record StudentSessionHistoryItem(
        Long sessionId,
        LocalDate sessionDate,
        String topic,
        AttendanceStatus status,
        String remarks
) {}
