package com.campusconnect.patterns.state;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;

public class SubmittedState extends AbstractEnrollmentState {

    @Override
    public void approve(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.APPROVED);
    }

    @Override
    public void reject(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.REJECTED);
    }

    @Override
    public void drop(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.DROPPED);
    }

    @Override
    public EnrollmentStatus getStatus() {
        return EnrollmentStatus.SUBMITTED;
    }
}
