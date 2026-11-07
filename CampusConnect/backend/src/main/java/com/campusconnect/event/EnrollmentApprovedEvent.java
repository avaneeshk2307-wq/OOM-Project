package com.campusconnect.event;

public record EnrollmentApprovedEvent(
        Long studentId,
        Long courseId,
        String courseCode,
        String courseTitle
) {}
