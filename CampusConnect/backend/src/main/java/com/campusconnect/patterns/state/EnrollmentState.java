package com.campusconnect.patterns.state;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;

/**
 * EnrollmentState - State Pattern interface governing the enrollment lifecycle.
 * Demonstrates:
 * 1. State Design Pattern - Encapsulates state-specific behavior and transition logic.
 * 2. Open/Closed Principle (OCP) - New enrollment states can be added without modifying context code.
 */
public interface EnrollmentState {
    void submit(Enrollment enrollment);
    void approve(Enrollment enrollment);
    void finalizeEnrollment(Enrollment enrollment);
    void drop(Enrollment enrollment);
    void reject(Enrollment enrollment);
    EnrollmentStatus getStatus();
}
