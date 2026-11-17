package com.campusconnect.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Student - Concrete domain model extending Person.
 * Demonstrates:
 * 1. Inheritance - Inherits common identity from Person using JOINED strategy.
 * 2. Encapsulation - Semester promotion and department changes governed by business invariants.
 * 3. Association - Associated with Department and Enrollment.
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends Person {

    @Column(name = "roll_number", nullable = false, unique = true, length = 50)
    private String rollNumber;

    @Column(nullable = false)
    private int semester = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    protected Student() {
        super();
    }

    public Student(String name, String email, String phone, String rollNumber, int semester, Department department) {
        super(name, email, phone);
        this.rollNumber = Objects.requireNonNull(rollNumber, "Roll number must not be null").trim();
        setSemester(semester);
        this.department = department;
    }

    // Business Methods enforcing Encapsulation
    public void promoteToNextSemester() {
        if (this.semester >= 12) {
            throw new IllegalStateException("Student has already reached the maximum academic semester (12).");
        }
        this.semester++;
    }

    public void setSemester(int semester) {
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Semester must be between 1 and 12. Provided: " + semester);
        }
        this.semester = semester;
    }

    public void assignDepartment(Department department) {
        this.department = department;
    }

    // Getters
    public String getRollNumber() {
        return rollNumber;
    }

    public int getSemester() {
        return semester;
    }

    public Department getDepartment() {
        return department;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }
}
