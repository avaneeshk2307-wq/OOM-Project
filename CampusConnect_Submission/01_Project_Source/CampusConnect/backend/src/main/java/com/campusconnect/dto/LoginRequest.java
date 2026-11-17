package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - Immutable DTO for incoming user credentials.
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
