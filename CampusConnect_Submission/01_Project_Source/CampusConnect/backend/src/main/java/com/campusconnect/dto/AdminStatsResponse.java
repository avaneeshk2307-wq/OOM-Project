package com.campusconnect.dto;

import java.util.Map;

public record AdminStatsResponse(
        long totalStudents,
        long totalFaculty,
        long totalCourses,
        long totalDepartments,
        long totalEnrollments,
        Map<String, Long> studentsByDepartment
) {
}
