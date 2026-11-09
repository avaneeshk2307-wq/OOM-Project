package com.campusconnect.dto;

import java.util.Map;

public record CourseGradeDistributionResponse(
        Long courseId,
        String courseCode,
        String courseTitle,
        int totalGradedAssessments,
        double averagePercentage,
        double standardDeviation,
        double highestPercentage,
        double lowestPercentage,
        Map<String, Long> absoluteDistribution,
        Map<String, Long> relativeDistribution
) {}

