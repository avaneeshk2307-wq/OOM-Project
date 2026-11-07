package com.campusconnect.patterns.state;

import com.campusconnect.enums.EnrollmentStatus;

/**
 * EnrollmentStateFactory - Factory creating the appropriate state pattern instance.
 */
public final class EnrollmentStateFactory {

    private EnrollmentStateFactory() {
    }

    public static EnrollmentState getState(EnrollmentStatus status) {
        return switch (status) {
            case DRAFT -> new DraftState();
            case SUBMITTED -> new SubmittedState();
            case APPROVED -> new ApprovedState();
            case ENROLLED -> new EnrolledState();
            case DROPPED -> new DroppedState();
            case REJECTED -> new RejectedState();
        };
    }
}
