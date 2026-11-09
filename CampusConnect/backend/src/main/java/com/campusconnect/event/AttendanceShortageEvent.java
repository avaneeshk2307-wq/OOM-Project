package com.campusconnect.event;

public record AttendanceShortageEvent(
        Long studentId,
        Long courseId,
        String courseCode,
        long attendedSessions,
        long totalSessions,
        double attendancePercentage
) {}

