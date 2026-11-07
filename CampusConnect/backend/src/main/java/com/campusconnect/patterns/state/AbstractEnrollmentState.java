package com.campusconnect.patterns.state;

import com.campusconnect.entity.Enrollment;

/**
 * AbstractEnrollmentState - Base class providing default illegal transition behaviors.
 * Subclasses override only the operations valid for their specific state.
 */
public abstract class AbstractEnrollmentState implements EnrollmentState {

    @Override
    public void submit(Enrollment enrollment) {
        throw new IllegalStateException("Cannot submit an enrollment in state: " + getStatus());
    }

    @Override
    public void approve(Enrollment enrollment) {
        throw new IllegalStateException("Cannot approve an enrollment in state: " + getStatus());
    }

    @Override
    public void finalizeEnrollment(Enrollment enrollment) {
        throw new IllegalStateException("Cannot finalize an enrollment into ENROLLED in state: " + getStatus());
    }

    @Override
    public void drop(Enrollment enrollment) {
        throw new IllegalStateException("Cannot drop an enrollment in state: " + getStatus());
    }

    @Override
    public void reject(Enrollment enrollment) {
        throw new IllegalStateException("Cannot reject an enrollment in state: " + getStatus());
    }
}
