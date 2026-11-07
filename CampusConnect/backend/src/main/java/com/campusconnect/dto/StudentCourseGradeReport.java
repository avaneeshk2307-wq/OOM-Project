package com.campusconnect.dto;

import java.math.BigDecimal;
import java.util.List;

public record StudentCourseGradeReport(
        Long courseId,
        String courseCode,
        String courseTitle,
        int credits,
        Long enrollmentId,
        List<GradeResponse> assessments,
        BigDecimal totalWeightedScore,
        String finalGrade,
        double gradePoint
) {}
