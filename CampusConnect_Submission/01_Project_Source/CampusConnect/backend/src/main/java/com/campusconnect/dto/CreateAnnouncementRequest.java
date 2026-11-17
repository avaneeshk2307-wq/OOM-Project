package com.campusconnect.dto;

import com.campusconnect.enums.Role;
import jakarta.validation.constraints.NotBlank;

public record CreateAnnouncementRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        Role targetRole,

        Long courseId
) {}
