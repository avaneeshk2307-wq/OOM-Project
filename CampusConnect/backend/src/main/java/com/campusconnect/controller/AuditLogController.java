package com.campusconnect.controller;

import com.campusconnect.dto.AuditLogResponse;
import com.campusconnect.enums.AuditAction;
import com.campusconnect.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogResponse> logs = (action != null)
                ? auditLogService.getAuditLogsByAction(action, pageable)
                : auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogResponse>> getRecentAuditLogs() {
        return ResponseEntity.ok(auditLogService.getRecentAuditLogs());
    }
}

