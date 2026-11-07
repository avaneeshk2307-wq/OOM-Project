package com.campusconnect.dto;

import java.util.List;
import java.util.Map;

public record CampusAnalyticsResponse(
        List<DepartmentMetric> departmentMetrics,
        List<CourseCapacityMetric> courseCapacityMetrics,
        AttendanceHealthMetric attendanceHealth,
        Map<String, Long> gradeDistribution,
        List<FacultyWorkloadMetric> facultyWorkload
) {
    public record DepartmentMetric(
            Long departmentId,
            String code,
            String name,
            long studentCount,
            long facultyCount,
            long courseCount
    ) {}

    public record CourseCapacityMetric(
            Long courseId,
            String code,
            String title,
            long enrolledStudents,
            int capacity,
            double utilizationPercentage
    ) {}

    public record AttendanceHealthMetric(
            long totalSessionRecords,
            long totalPresentRecords,
            double campusWidePercentage,
            long studentsInGoodStanding,
            long studentsWithShortageAlert
    ) {}

    public record FacultyWorkloadMetric(
            Long facultyId,
            String facultyName,
            String departmentCode,
            long assignedCoursesCount,
            int totalCreditsTaught,
            long weeklyScheduledSlots
    ) {}
}
