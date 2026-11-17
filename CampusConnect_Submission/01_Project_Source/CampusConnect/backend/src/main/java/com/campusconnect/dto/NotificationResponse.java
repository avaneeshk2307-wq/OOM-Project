package com.campusconnect.dto;

import com.campusconnect.enums.NotificationType;
import java.time.Instant;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean isRead,
        Instant createdAt
) {}

