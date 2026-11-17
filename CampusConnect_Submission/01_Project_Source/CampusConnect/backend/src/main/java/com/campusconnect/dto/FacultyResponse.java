package com.campusconnect.dto;

import com.campusconnect.enums.AccountStatus;
import java.time.Instant;

public record FacultyResponse(
        Long id,
        String name,
        String email,
        String phone,
        String employeeCode,
        String designation,
        Long departmentId,
        String departmentName,
        int courseCount,
        AccountStatus accountStatus,
        Instant createdAt
) {
}
