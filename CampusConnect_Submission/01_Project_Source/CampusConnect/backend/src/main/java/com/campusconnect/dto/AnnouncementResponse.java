package com.campusconnect.dto;

import com.campusconnect.enums.Role;
import java.time.Instant;

public record AnnouncementResponse(
        Long id,
        String title,
        String content,
        Role targetRole,
        Long courseId,
        String courseCode,
        String authorName,
        Instant createdAt
) {}
