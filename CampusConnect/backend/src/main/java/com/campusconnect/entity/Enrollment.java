package com.campusconnect.entity;

import com.campusconnect.enums.EnrollmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Enrollment - Models a student's registration in a course.
 * Demonstrates:
 * 1. Association - Connects Student and Course.
 * 2. State Pattern Context - Encapsulates lifecycle states (Draft -> Submitted -> Approved -> Enrolled -> Dropped/Rejected)
 *    and forbids illegal transitions.
 * 3. Encapsulation - Guards status changes via explicit business actions.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(name = "uq_student_course_semester", columnNames = {"student_id", "course_id", "semester"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private int semester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EnrollmentStatus status = EnrollmentStatus.DRAFT;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt = Instant.now();

    protected Enrollment() {
    }

    public Enrollment(Student student, Course course, int semester) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.course = Objects.requireNonNull(course, "Course must not be null");
        this.semester = semester;
        this.status = EnrollmentStatus.DRAFT;
        this.registeredAt = Instant.now();
    }

    // State Lifecycle Business Methods enforcing State Invariants
    public void submit() {
        if (this.status != EnrollmentStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT enrollments can be submitted. Current: " + this.status);
        }
        this.status = EnrollmentStatus.SUBMITTED;
    }

    public void approve() {
        if (this.status != EnrollmentStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED enrollments can be approved. Current: " + this.status);
        }
        this.status = EnrollmentStatus.APPROVED;
    }

    public void finalizeEnrollment() {
        if (this.status != EnrollmentStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED enrollments can be finalized into ENROLLED. Current: " + this.status);
        }
        this.status = EnrollmentStatus.ENROLLED;
    }

    public void drop() {
        if (this.status != EnrollmentStatus.ENROLLED && this.status != EnrollmentStatus.APPROVED && this.status != EnrollmentStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot drop an enrollment in state: " + this.status);
        }
        this.status = EnrollmentStatus.DROPPED;
    }

    public void reject() {
        if (this.status != EnrollmentStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED enrollments can be rejected. Current: " + this.status);
        }
        this.status = EnrollmentStatus.REJECTED;
    }

    public void transitionTo(EnrollmentStatus targetStatus) {
        switch (targetStatus) {
            case SUBMITTED -> submit();
            case APPROVED -> approve();
            case ENROLLED -> finalizeEnrollment();
            case DROPPED -> drop();
            case REJECTED -> reject();
            default -> throw new IllegalArgumentException("Unsupported enrollment transition to: " + targetStatus);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public int getSemester() {
        return semester;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
