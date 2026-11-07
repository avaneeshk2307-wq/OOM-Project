package com.campusconnect.enums;

/**
 * EnrollmentStatus - Represents the formal lifecycle state of course registration.
 * Used in conjunction with the State Design Pattern.
 */
public enum EnrollmentStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    ENROLLED,
    DROPPED,
    REJECTED
}
