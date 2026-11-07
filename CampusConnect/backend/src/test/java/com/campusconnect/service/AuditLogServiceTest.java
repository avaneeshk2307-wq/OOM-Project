package com.campusconnect.service;

import com.campusconnect.dto.AuditLogResponse;
import com.campusconnect.entity.AuditLog;
import com.campusconnect.enums.AuditAction;
import com.campusconnect.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    @DisplayName("Should persist audit log entry accurately with metadata")
    void testRecordLog() {
        AuditAction action = AuditAction.CREATE_STUDENT;
        String entityName = "Student";
        String entityId = "101";
        String performedBy = "admin";
        String details = "Created student roll CS2026-001";
        String ipAddress = "192.168.1.50";

        auditLogService.recordLog(action, entityName, entityId, performedBy, details, ipAddress);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getAction()).isEqualTo(action);
        assertThat(saved.getEntityName()).isEqualTo(entityName);
        assertThat(saved.getEntityId()).isEqualTo(entityId);
        assertThat(saved.getPerformedByUsername()).isEqualTo(performedBy);
        assertThat(saved.getDetails()).isEqualTo(details);
        assertThat(saved.getIpAddress()).isEqualTo(ipAddress);
    }

    @Test
    @DisplayName("Should retrieve paginated audit logs successfully")
    void testGetAuditLogs() {
        AuditLog log = new AuditLog(AuditAction.SUBMIT_GRADE, "Grade", "55", "faculty1", "Graded quiz", "127.0.0.1");
        Pageable pageable = PageRequest.of(0, 10);
        when(auditLogRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        Page<AuditLogResponse> result = auditLogService.getAuditLogs(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).action()).isEqualTo(AuditAction.SUBMIT_GRADE);
        assertThat(result.getContent().get(0).performedByUsername()).isEqualTo("faculty1");
    }

    @Test
    @DisplayName("Should retrieve recent audit logs correctly")
    void testGetRecentAuditLogs() {
        AuditLog log = new AuditLog(AuditAction.ASSIGN_FACULTY, "Course", "12", "admin", "Assigned instructor", "127.0.0.1");
        when(auditLogRepository.findTop50ByOrderByCreatedAtDesc()).thenReturn(List.of(log));

        List<AuditLogResponse> recent = auditLogService.getRecentAuditLogs();

        assertThat(recent).hasSize(1);
        assertThat(recent.get(0).action()).isEqualTo(AuditAction.ASSIGN_FACULTY);
    }
}
