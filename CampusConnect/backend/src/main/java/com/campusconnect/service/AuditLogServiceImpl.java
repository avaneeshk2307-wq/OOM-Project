package com.campusconnect.service;

import com.campusconnect.dto.AuditLogResponse;
import com.campusconnect.entity.AuditLog;
import com.campusconnect.enums.AuditAction;
import com.campusconnect.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLog(AuditAction action, String entityName, String entityId, String performedBy, String details, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog(action, entityName, entityId, performedBy, details, ipAddress);
            auditLogRepository.save(auditLog);
            log.info("AUDIT_LOG: action={}, entity={}/{}, user={}", action, entityName, entityId, performedBy);
        } catch (Exception e) {
            log.error("Failed to persist audit log for action: {}", action, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLog(AuditAction action, String entityName, String entityId, String details) {
        String username = "SYSTEM";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            username = auth.getName();
        }
        recordLog(action, entityName, entityId, username, details, "127.0.0.1");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(AuditLogResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable)
                .map(AuditLogResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }
}
