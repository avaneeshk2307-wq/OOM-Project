package com.campusconnect.controller;

import com.campusconnect.dto.EnrollmentRegisterRequest;
import com.campusconnect.dto.EnrollmentResponse;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.repository.UserAccountRepository;
import com.campusconnect.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserAccountRepository userAccountRepository;

    public EnrollmentController(EnrollmentService enrollmentService, UserAccountRepository userAccountRepository) {
        this.enrollmentService = enrollmentService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponse> register(
            @Valid @RequestBody EnrollmentRegisterRequest request,
            Authentication authentication
    ) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        Long studentId = account.getPerson().getId();
        EnrollmentResponse response = enrollmentService.registerCourse(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/student/{studentId}/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> registerForStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody EnrollmentRegisterRequest request
    ) {
        EnrollmentResponse response = enrollmentService.registerCourse(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments(
            @RequestParam(required = false) EnrollmentStatus status
    ) {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(status));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/my-enrollments")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(Authentication authentication) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(account.getPerson().getId()));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<EnrollmentResponse>> getCourseEnrollments(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.approveEnrollment(id));
    }

    @PutMapping("/{id}/finalize")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> finalizeEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.finalizeEnrollment(id));
    }

    @PutMapping("/{id}/drop")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponse> drop(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.dropEnrollment(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.rejectEnrollment(id));
    }
}
