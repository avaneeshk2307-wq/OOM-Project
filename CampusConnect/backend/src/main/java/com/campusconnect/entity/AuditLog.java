package com.campusconnect.entity;

import com.campusconnect.enums.AuditAction;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity recording immutable administrative, academic, and security audit logs.
 * Demonstrates Single Responsibility, Auditability, and Information Hiding.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Column(name = "performed_by_username", nullable = false, length = 100)
    private String performedByUsername;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {
        // JPA
    }

    public AuditLog(
            AuditAction action,
            String entityName,
            String entityId,
            String performedByUsername,
            String details,
            String ipAddress
    ) {
        this.action = Objects.requireNonNull(action, "Audit action must not be null");
        this.entityName = Objects.requireNonNull(entityName, "Entity name must not be null");
        this.entityId = entityId;
        this.performedByUsername = performedByUsername != null ? performedByUsername : "SYSTEM";
        this.details = details;
        this.ipAddress = ipAddress != null ? ipAddress : "127.0.0.1";
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPerformedByUsername() {
        return performedByUsername;
    }

    public void setPerformedByUsername(String performedByUsername) {
        this.performedByUsername = performedByUsername;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
