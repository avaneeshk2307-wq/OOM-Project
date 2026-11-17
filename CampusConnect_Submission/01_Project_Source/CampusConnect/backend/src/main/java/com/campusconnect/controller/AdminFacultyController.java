package com.campusconnect.controller;

import com.campusconnect.dto.CreateFacultyRequest;
import com.campusconnect.dto.FacultyResponse;
import com.campusconnect.dto.UpdateFacultyRequest;
import com.campusconnect.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/faculty")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminFacultyController {

    private final FacultyService facultyService;

    public AdminFacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<FacultyResponse> createFaculty(@Valid @RequestBody CreateFacultyRequest request) {
        FacultyResponse created = facultyService.createFaculty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<FacultyResponse>> getAllFaculty(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        int boundedPage = Math.max(0, page);
        int boundedSize = Math.min(Math.max(1, size), 100);
        String[] sortParts = sort.split(",");
        org.springframework.data.domain.Sort.Direction dir = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc"))
                ? org.springframework.data.domain.Sort.Direction.DESC
                : org.springframework.data.domain.Sort.Direction.ASC;
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(boundedPage, boundedSize, org.springframework.data.domain.Sort.by(dir, sortParts[0].trim()));
        return ResponseEntity.ok(facultyService.getAllFacultyPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponse> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFacultyRequest request
    ) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(Map.of("message", "Faculty member removed successfully"));
    }
}
