package com.campusconnect.dto;

import java.util.List;

public record StudentCourseAttendanceSummary(
        Long courseId,
        String courseCode,
        String courseTitle,
        int credits,
        long totalSessions,
        long attendedSessions,
        double percentage,
        boolean lowAttendanceWarning,
        List<StudentSessionHistoryItem> history
) {}
