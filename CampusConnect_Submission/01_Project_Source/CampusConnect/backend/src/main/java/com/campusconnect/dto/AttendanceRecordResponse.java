package com.campusconnect.dto;

import com.campusconnect.enums.AttendanceStatus;

public record AttendanceRecordResponse(
        Long id,
        Long studentId,
        String studentName,
        String rollNumber,
        AttendanceStatus status,
        String remarks
) {}
