package com.campusconnect.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Faculty - Concrete domain model extending Person representing teaching staff.
 * Demonstrates:
 * 1. Inheritance - Subclass of Person in the JOINED table hierarchy.
 * 2. Association - Bi-directional association with Course (assigned teaching workload).
 * 3. Encapsulation - Controlled mutation of professional designation and academic department.
 */
@Entity
@Table(name = "faculty")
@PrimaryKeyJoinColumn(name = "id")
public class Faculty extends Person {

    @Column(name = "employee_code", nullable = false, unique = true, length = 50)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    private List<Course> assignedCourses = new ArrayList<>();

    protected Faculty() {
        super();
    }

    public Faculty(String name, String email, String phone, String employeeCode, String designation, Department department) {
        super(name, email, phone);
        this.employeeCode = Objects.requireNonNull(employeeCode, "Employee code must not be null").trim();
        this.designation = Objects.requireNonNull(designation, "Designation must not be null").trim();
        this.department = department;
    }

    public void updateDesignation(String newDesignation) {
        if (newDesignation != null && !newDesignation.trim().isEmpty()) {
            this.designation = newDesignation.trim();
        }
    }

    public void assignDepartment(Department department) {
        this.department = department;
    }

    // Getters
    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getDesignation() {
        return designation;
    }

    public Department getDepartment() {
        return department;
    }

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }
}
