package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.repository.UserAccountRepository;
import com.campusconnect.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
public class GradeController {

    private final GradeService gradeService;
    private final UserAccountRepository userAccountRepository;

    public GradeController(GradeService gradeService, UserAccountRepository userAccountRepository) {
        this.gradeService = gradeService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/assessments")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AssessmentResponse> createAssessment(
            @Valid @RequestBody CreateAssessmentRequest request,
            Authentication authentication
    ) {
        AssessmentResponse response = gradeService.createAssessment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/assessments/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<AssessmentResponse>> getAssessmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getAssessmentsByCourse(courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<GradeResponse> recordGrade(
            @Valid @RequestBody SubmitGradeRequest request,
            Authentication authentication
    ) {
        GradeResponse response = gradeService.recordGrade(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<GradeResponse>> recordBatchGrades(
            @Valid @RequestBody List<SubmitGradeRequest> requests,
            Authentication authentication
    ) {
        List<GradeResponse> responses = gradeService.recordBatchGrades(requests, authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<GradeResponse>> getGradesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/distribution")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<CourseGradeDistributionResponse> getCourseGradeDistribution(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getCourseGradeDistribution(courseId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<StudentCourseGradeReport>> getStudentGradeReports(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getStudentGradeReports(studentId));
    }

    @GetMapping("/my-grades")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<StudentCourseGradeReport>> getMyGrades(Authentication authentication) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        return ResponseEntity.ok(gradeService.getStudentGradeReports(account.getPerson().getId()));
    }
}
