package com.campusconnect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

public record TimetableResponse(
        Long id,
        Long courseId,
        String courseCode,
        String courseTitle,
        Long classroomId,
        String roomNumber,
        String building,
        int classroomCapacity,
        Long facultyId,
        String facultyName,
        DayOfWeek dayOfWeek,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,
        Instant createdAt
) {}
