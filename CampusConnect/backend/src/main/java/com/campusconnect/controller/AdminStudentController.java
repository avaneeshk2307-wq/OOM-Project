package com.campusconnect.controller;

import com.campusconnect.dto.CreateStudentRequest;
import com.campusconnect.dto.StudentResponse;
import com.campusconnect.dto.UpdateStudentRequest;
import com.campusconnect.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/students")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminStudentController {

    private final StudentService studentService;

    public AdminStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse created = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<StudentResponse>> getAllStudents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester,
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
        return ResponseEntity.ok(studentService.getAllStudentsPaged(query, departmentId, semester, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<StudentResponse> promoteStudent(@PathVariable Long id) {
        StudentResponse promoted = studentService.promoteStudent(id);
        return ResponseEntity.ok(promoted);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("message", "Student profile removed successfully"));
    }
}
