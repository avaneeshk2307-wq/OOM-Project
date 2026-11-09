package com.campusconnect.controller;

import com.campusconnect.patterns.builder.AcademicTranscriptReport;
import com.campusconnect.service.TranscriptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transcripts")
public class TranscriptController {

    private final TranscriptService transcriptService;

    public TranscriptController(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    @GetMapping("/my-transcript")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<AcademicTranscriptReport> getMyTranscript(Authentication authentication) {
        return ResponseEntity.ok(transcriptService.generateTranscriptForUsername(authentication.getName()));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AcademicTranscriptReport> getStudentTranscript(@PathVariable Long studentId) {
        return ResponseEntity.ok(transcriptService.generateTranscriptForStudent(studentId));
    }
}

