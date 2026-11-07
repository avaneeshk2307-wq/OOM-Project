package com.campusconnect.service;

import com.campusconnect.dto.AuditLogResponse;
import com.campusconnect.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {

    void recordLog(AuditAction action, String entityName, String entityId, String performedBy, String details, String ipAddress);

    void recordLog(AuditAction action, String entityName, String entityId, String details);

    Page<AuditLogResponse> getAuditLogs(Pageable pageable);

    Page<AuditLogResponse> getAuditLogsByAction(AuditAction action, Pageable pageable);

    List<AuditLogResponse> getRecentAuditLogs();
}
