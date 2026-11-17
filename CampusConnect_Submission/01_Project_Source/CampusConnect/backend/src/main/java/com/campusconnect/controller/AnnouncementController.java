package com.campusconnect.controller;

import com.campusconnect.dto.AnnouncementResponse;
import com.campusconnect.dto.CreateAnnouncementRequest;
import com.campusconnect.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_FACULTY')")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            Authentication authentication
    ) {
        AnnouncementResponse response = announcementService.createAnnouncement(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getMyAnnouncements(Authentication authentication) {
        return ResponseEntity.ok(announcementService.getAnnouncementsForUser(authentication.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAnnouncement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        announcementService.deleteAnnouncement(id, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Announcement deleted successfully"));
    }
}
