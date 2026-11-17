package com.campusconnect.entity;

import com.campusconnect.enums.AttendanceStatus;
import jakarta.persistence.*;
import java.util.Objects;

/**
 * AttendanceRecord - Individual student presence record within an AttendanceSession.
 * Demonstrates:
 * 1. Composition - Life cycle completely bound to parent AttendanceSession.
 * 2. Encapsulation - Controlled status and remarks updates.
 */
@Entity
@Table(name = "attendance_records", uniqueConstraints = {
        @UniqueConstraint(name = "uq_session_student", columnNames = {"session_id", "student_id"})
})
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(length = 255)
    private String remarks;

    protected AttendanceRecord() {
    }

    public AttendanceRecord(AttendanceSession session, Student student, AttendanceStatus status, String remarks) {
        this.session = Objects.requireNonNull(session, "Session must not be null");
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.status = Objects.requireNonNull(status, "Status must not be null");
        this.remarks = remarks;
    }

    public void updateStatus(AttendanceStatus newStatus, String remarks) {
        this.status = Objects.requireNonNull(newStatus, "Status must not be null");
        this.remarks = remarks;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public AttendanceSession getSession() {
        return session;
    }

    public Student getStudent() {
        return student;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceRecord that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
