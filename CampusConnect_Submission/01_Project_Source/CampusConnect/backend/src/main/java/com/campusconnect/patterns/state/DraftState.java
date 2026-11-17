package com.campusconnect.patterns.state;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;

public class DraftState extends AbstractEnrollmentState {

    @Override
    public void submit(Enrollment enrollment) {
        enrollment.transitionTo(EnrollmentStatus.SUBMITTED);
    }

    @Override
    public EnrollmentStatus getStatus() {
        return EnrollmentStatus.DRAFT;
    }
}
