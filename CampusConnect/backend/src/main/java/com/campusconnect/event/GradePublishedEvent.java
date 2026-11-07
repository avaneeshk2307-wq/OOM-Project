package com.campusconnect.event;

import java.math.BigDecimal;

public record GradePublishedEvent(
        Long studentId,
        Long courseId,
        String courseCode,
        String assessmentName,
        BigDecimal marksObtained,
        BigDecimal maxMarks,
        String letterGrade
) {}
