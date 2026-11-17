package com.campusconnect.service;

import com.campusconnect.dto.CreateStudentRequest;
import com.campusconnect.dto.StudentResponse;
import com.campusconnect.dto.UpdateStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(CreateStudentRequest request);
    StudentResponse getStudentById(Long id);
    List<StudentResponse> getAllStudents(String query, Long departmentId, Integer semester);
    Page<StudentResponse> getAllStudentsPaged(String query, Long departmentId, Integer semester, Pageable pageable);
    StudentResponse updateStudent(Long id, UpdateStudentRequest request);
    StudentResponse promoteStudent(Long id);
    void deleteStudent(Long id);
}
