package com.campusconnect.dto;

import com.campusconnect.enums.AssessmentType;
import java.math.BigDecimal;
import java.time.Instant;

public record GradeResponse(
        Long id,
        Long enrollmentId,
        Long studentId,
        String studentName,
        String rollNumber,
        Long assessmentId,
        String assessmentName,
        AssessmentType assessmentType,
        BigDecimal marksObtained,
        BigDecimal maxMarks,
        String letterGrade,
        double gradePoint,
        String remark,
        Instant gradedAt
) {}
