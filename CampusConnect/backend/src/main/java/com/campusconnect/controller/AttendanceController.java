package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.repository.UserAccountRepository;
import com.campusconnect.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserAccountRepository userAccountRepository;

    public AttendanceController(AttendanceService attendanceService, UserAccountRepository userAccountRepository) {
        this.attendanceService = attendanceService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AttendanceSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            Authentication authentication
    ) {
        AttendanceSessionResponse response = attendanceService.createSession(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sessions/{sessionId}/records")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AttendanceSessionResponse> markAttendance(
            @PathVariable Long sessionId,
            @Valid @RequestBody List<RecordEntryRequest> records,
            Authentication authentication
    ) {
        AttendanceSessionResponse response = attendanceService.markAttendance(sessionId, records, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<AttendanceSessionResponse>> getSessionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getSessionsByCourse(courseId));
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AttendanceSessionResponse> getSessionById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceService.getSessionById(sessionId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<StudentCourseAttendanceSummary>> getStudentAttendanceSummary(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendanceSummary(studentId));
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<StudentCourseAttendanceSummary>> getMyAttendance(Authentication authentication) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        return ResponseEntity.ok(attendanceService.getStudentAttendanceSummary(account.getPerson().getId()));
    }
}
