package com.campusconnect.dto;

import com.campusconnect.enums.Role;

/**
 * LoginResponse - Immutable DTO returning authenticated session metadata and JWT token.
 */
public record LoginResponse(
        String token,
        String tokenType,
        Long userId,
        Long personId,
        String username,
        String fullName,
        String email,
        Role role
) {
    public static LoginResponse of(
            String token,
            Long userId,
            Long personId,
            String username,
            String fullName,
            String email,
            Role role
    ) {
        return new LoginResponse(
                token,
                "Bearer",
                userId,
                personId,
                username,
                fullName,
                email,
                role
        );
    }
}
