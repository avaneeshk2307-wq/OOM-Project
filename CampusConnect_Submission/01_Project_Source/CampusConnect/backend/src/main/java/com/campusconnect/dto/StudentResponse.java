package com.campusconnect.dto;

import com.campusconnect.enums.AccountStatus;
import java.time.Instant;

public record StudentResponse(
        Long id,
        String name,
        String email,
        String phone,
        String rollNumber,
        int semester,
        Long departmentId,
        String departmentName,
        int enrolledCoursesCount,
        AccountStatus accountStatus,
        Instant createdAt
) {
}
