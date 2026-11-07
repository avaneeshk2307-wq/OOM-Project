package com.campusconnect.service;

import com.campusconnect.dto.AdminStatsResponse;
import com.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AdminDashboardServiceImpl(
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            CourseRepository courseRepository,
            DepartmentRepository departmentRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getSystemStatistics() {
        long totalStudents = studentRepository.count();
        long totalFaculty = facultyRepository.count();
        long totalCourses = courseRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        Map<String, Long> studentsByDepartment = new HashMap<>();
        departmentRepository.findAll().forEach(dept -> {
            long count = studentRepository.findByDepartmentId(dept.getId()).size();
            studentsByDepartment.put(dept.getName(), count);
        });

        return new AdminStatsResponse(
                totalStudents,
                totalFaculty,
                totalCourses,
                totalDepartments,
                totalEnrollments,
                studentsByDepartment
        );
    }
}
