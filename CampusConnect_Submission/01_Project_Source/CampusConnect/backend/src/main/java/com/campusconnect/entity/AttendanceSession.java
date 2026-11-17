package com.campusconnect.entity;

import com.campusconnect.enums.AttendanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AttendanceSession - Models an academic class meeting for a course.
 * Demonstrates:
 * 1. Composition - Strong lifecycle ownership of AttendanceRecord objects
 *    (cascaded persistence and orphan removal).
 * 2. Encapsulation - Provides methods to mark, update, and compute attendance counts.
 */
@Entity
@Table(name = "attendance_sessions")
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(length = 200)
    private String topic;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttendanceRecord> records = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AttendanceSession() {
    }

    public AttendanceSession(Course course, LocalDate sessionDate, String topic) {
        this.course = Objects.requireNonNull(course, "Course must not be null");
        this.sessionDate = Objects.requireNonNull(sessionDate, "Session date must not be null");
        this.topic = topic != null ? topic.trim() : "";
        this.createdAt = Instant.now();
    }

    // Business Methods enforcing Composition
    public AttendanceRecord recordStudentAttendance(Student student, AttendanceStatus status, String remarks) {
        Objects.requireNonNull(student, "Student must not be null");
        Objects.requireNonNull(status, "Status must not be null");

        // If record already exists for this student in this session, update it
        for (AttendanceRecord existing : this.records) {
            if (existing.getStudent().equals(student)) {
                existing.updateStatus(status, remarks);
                return existing;
            }
        }

        // Otherwise create new record and attach to this composition root
        AttendanceRecord newRecord = new AttendanceRecord(this, student, status, remarks);
        this.records.add(newRecord);
        return newRecord;
    }

    public long getPresentCount() {
        return records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.LATE)
                .count();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public String getTopic() {
        return topic;
    }

    public List<AttendanceRecord> getRecords() {
        return records;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceSession that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
