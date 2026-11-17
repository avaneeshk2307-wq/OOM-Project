package com.campusconnect.dto;

import com.campusconnect.entity.AuditLog;
import com.campusconnect.enums.AuditAction;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        AuditAction action,
        String entityName,
        String entityId,
        String performedByUsername,
        String details,
        String ipAddress,
        Instant createdAt
) {
    public static AuditLogResponse fromEntity(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getEntityName(),
                log.getEntityId(),
                log.getPerformedByUsername(),
                log.getDetails(),
                log.getIpAddress(),
                log.getCreatedAt()
        );
    }
}

