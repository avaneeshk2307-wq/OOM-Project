package com.campusconnect.patterns.state;

import com.campusconnect.enums.EnrollmentStatus;

public class RejectedState extends AbstractEnrollmentState {

    @Override
    public EnrollmentStatus getStatus() {
        return EnrollmentStatus.REJECTED;
    }
}
