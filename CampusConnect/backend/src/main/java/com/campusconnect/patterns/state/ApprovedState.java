package com.campusconnect.patterns.state;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;

public class ApprovedState extends AbstractEnrollmentState {

    @Override
    public void finalizeEnrollment(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.ENROLLED);
    }

    @Override
    public void drop(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.DROPPED);
    }

    @Override
    public EnrollmentStatus getStatus() {
        return EnrollmentStatus.APPROVED;
    }
}
