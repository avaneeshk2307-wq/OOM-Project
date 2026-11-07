package com.campusconnect.dto;

import com.campusconnect.enums.Role;

/**
 * UserProfileResponse - Returns currently authenticated user details (/api/v1/auth/me).
 */
public record UserProfileResponse(
        Long userId,
        Long personId,
        String username,
        String fullName,
        String email,
        Role role,
        String roleSpecificCode, // rollNumber for Student, employeeCode for Faculty, accessLevel for Admin
        String departmentName
) {
}
