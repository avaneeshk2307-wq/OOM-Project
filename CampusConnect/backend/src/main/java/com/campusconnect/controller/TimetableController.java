package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.UserAccountRepository;
import com.campusconnect.service.TimetableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final UserAccountRepository userAccountRepository;

    public TimetableController(TimetableService timetableService, UserAccountRepository userAccountRepository) {
        this.timetableService = timetableService;
        this.userAccountRepository = userAccountRepository;
    }

    // --- Timetable Endpoints ---

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TimetableResponse> createTimetableEntry(@Valid @RequestBody CreateTimetableRequest request) {
        TimetableResponse response = timetableService.createTimetableEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<TimetableResponse>> getAllTimetables() {
        return ResponseEntity.ok(timetableService.getAllTimetables());
    }

    @GetMapping("/my-timetable")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<TimetableResponse>> getMyTimetable(Authentication authentication) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        if (account.getRole() == Role.ROLE_STUDENT) {
            return ResponseEntity.ok(timetableService.getTimetablesForStudent(account.getPerson().getId()));
        } else if (account.getRole() == Role.ROLE_FACULTY) {
            return ResponseEntity.ok(timetableService.getTimetablesByFaculty(account.getPerson().getId()));
        } else {
            return ResponseEntity.ok(timetableService.getAllTimetables());
        }
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(timetableService.getTimetablesByCourse(courseId));
    }

    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(timetableService.getTimetablesByFaculty(facultyId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<List<TimetableResponse>> getTimetablesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(timetableService.getTimetablesForStudent(studentId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTimetableEntry(@PathVariable Long id) {
        timetableService.deleteTimetableEntry(id);
        return ResponseEntity.noContent().build();
    }

    // --- Classroom Endpoints ---

    @PostMapping("/classrooms")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody CreateClassroomRequest request) {
        ClassroomResponse response = timetableService.createClassroom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/classrooms")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY', 'ROLE_STUDENT')")
    public ResponseEntity<List<ClassroomResponse>> getAllClassrooms() {
        return ResponseEntity.ok(timetableService.getAllClassrooms());
    }

    @DeleteMapping("/classrooms/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        timetableService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }
}

