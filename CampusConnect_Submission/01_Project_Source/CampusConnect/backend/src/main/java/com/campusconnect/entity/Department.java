package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Department - Represents an academic faculty division (e.g., Computer Science, Electrical Eng).
 * Demonstrates:
 * 1. Aggregation - Weak whole-part relationship with Course (courses belong to a department,
 *    but courses can persist independently or be reorganized without destroying course history).
 * 2. Encapsulation - Internal lists guarded; business validation for department code and name.
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Faculty> facultyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    protected Department() {
    }

    public Department(String code, String name, String description) {
        this.code = Objects.requireNonNull(code, "Department code must not be null").toUpperCase().trim();
        this.name = Objects.requireNonNull(name, "Department name must not be null").trim();
        this.description = description;
        this.createdAt = Instant.now();
    }

    public void updateDetails(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        this.description = description;
    }

    // Encapsulated getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Faculty> getFacultyMembers() {
        return facultyMembers;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
