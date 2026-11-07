package com.campusconnect.service;

import com.campusconnect.dto.EnrollmentRegisterRequest;
import com.campusconnect.dto.EnrollmentResponse;
import com.campusconnect.enums.EnrollmentStatus;
import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse registerCourse(Long studentId, EnrollmentRegisterRequest request);
    EnrollmentResponse approveEnrollment(Long enrollmentId);
    EnrollmentResponse finalizeEnrollment(Long enrollmentId);
    EnrollmentResponse dropEnrollment(Long enrollmentId);
    EnrollmentResponse rejectEnrollment(Long enrollmentId);
    List<EnrollmentResponse> getAllEnrollments(EnrollmentStatus status);
    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    EnrollmentResponse getEnrollmentById(Long id);
}
