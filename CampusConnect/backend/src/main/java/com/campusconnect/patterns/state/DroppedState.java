package com.campusconnect.patterns.state;

import com.campusconnect.enums.EnrollmentStatus;

public class DroppedState extends AbstractEnrollmentState {

    @Override
    public EnrollmentStatus getStatus() {
        return EnrollmentStatus.DROPPED;
    }
}
