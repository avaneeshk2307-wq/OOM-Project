package com.campusconnect.service;

import com.campusconnect.dto.CreateStudentRequest;
import com.campusconnect.dto.StudentResponse;
import com.campusconnect.dto.UpdateStudentRequest;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(CreateStudentRequest request);
    StudentResponse getStudentById(Long id);
    List<StudentResponse> getAllStudents(String query, Long departmentId, Integer semester);
    StudentResponse updateStudent(Long id, UpdateStudentRequest request);
    StudentResponse promoteStudent(Long id);
    void deleteStudent(Long id);
}
