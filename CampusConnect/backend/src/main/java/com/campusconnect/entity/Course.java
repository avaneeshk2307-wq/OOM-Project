package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Course - Represents an academic curriculum subject.
 * Demonstrates:
 * 1. Aggregation - Belongs to a Department (weak whole-part).
 * 2. Association - Taught by Faculty (instructor).
 * 3. Composition - Strong ownership of AssessmentComponent and AttendanceSession
 *    (cascaded lifecycle and orphan removal).
 * 4. Encapsulation - Validates credits, capacity, and provides safe assessment management.
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AssessmentComponent> assessmentComponents = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttendanceSession> attendanceSessions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Course() {
    }

    public Course(String code, String title, String description, int credits, int semester, int capacity, Department department, Faculty instructor) {
        this.code = Objects.requireNonNull(code, "Course code must not be null").toUpperCase().trim();
        this.title = Objects.requireNonNull(title, "Course title must not be null").trim();
        this.description = description;
        setCredits(credits);
        setSemester(semester);
        setCapacity(capacity);
        this.department = department;
        this.instructor = instructor;
        this.createdAt = Instant.now();
    }

    // Business Methods enforcing Encapsulation
    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Course credits must be strictly positive. Provided: " + credits);
        }
        this.credits = credits;
    }

    public void setSemester(int semester) {
        if (semester < 1) {
            throw new IllegalArgumentException("Course semester must be >= 1. Provided: " + semester);
        }
        this.semester = semester;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Course capacity must be strictly positive. Provided: " + capacity);
        }
        this.capacity = capacity;
    }

    public void updateDetails(String title, String description, int credits, int semester, int capacity) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        this.description = description;
        setCredits(credits);
        setSemester(semester);
        setCapacity(capacity);
    }

    public void assignInstructor(Faculty instructor) {
        this.instructor = instructor;
    }

    public void removeInstructor() {
        this.instructor = null;
    }

    public void addAssessmentComponent(AssessmentComponent component) {
        Objects.requireNonNull(component, "Assessment component must not be null");
        this.assessmentComponents.add(component);
        component.setCourse(this);
    }

    public void removeAssessmentComponent(AssessmentComponent component) {
        this.assessmentComponents.remove(component);
        component.setCourse(null);
    }

    public boolean hasAvailableSeats(long currentEnrolledCount) {
        return currentEnrolledCount < this.capacity;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCredits() {
        return credits;
    }

    public int getSemester() {
        return semester;
    }

    public int getCapacity() {
        return capacity;
    }

    public Department getDepartment() {
        return department;
    }

    public Faculty getInstructor() {
        return instructor;
    }

    public List<AssessmentComponent> getAssessmentComponents() {
        return assessmentComponents;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<AttendanceSession> getAttendanceSessions() {
        return attendanceSessions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return id != null && Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
