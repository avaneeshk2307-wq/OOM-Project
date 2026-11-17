package com.campusconnect.service;

import com.campusconnect.dto.CampusAnalyticsResponse;
import com.campusconnect.entity.Course;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.Faculty;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private TimetableRepository timetableRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Department department;
    private Faculty faculty;
    private Course course;

    @BeforeEach
    void setUp() {
        department = new Department("CS", "Computer Science", "CS Dept");
        faculty = new Faculty("Dr. Alan Turing", "turing@campus.edu", "555-0101", "FAC-001", "Professor", department);
        course = new Course("CS301", "Data Structures", "Intro DS", 4, 1, 50, department, faculty);
    }

    @Test
    @DisplayName("Should aggregate institutional analytics accurately across departments and courses")
    void testGetCampusAnalytics() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(studentRepository.findByDepartmentId(department.getId())).thenReturn(Collections.emptyList());
        when(facultyRepository.findByDepartmentId(department.getId())).thenReturn(Collections.emptyList());
        when(courseRepository.findByDepartmentId(department.getId())).thenReturn(List.of(course));

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ENROLLED)).thenReturn(25L);

        when(attendanceRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
        when(gradeRepository.findAll()).thenReturn(Collections.emptyList());
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));
        when(courseRepository.findByInstructorId(faculty.getId())).thenReturn(List.of(course));
        when(timetableRepository.findByFacultyId(faculty.getId())).thenReturn(Collections.emptyList());

        CampusAnalyticsResponse response = analyticsService.getCampusAnalytics();

        assertThat(response).isNotNull();
        assertThat(response.departmentMetrics()).hasSize(1);
        assertThat(response.departmentMetrics().get(0).code()).isEqualTo("CS");

        assertThat(response.courseCapacityMetrics()).hasSize(1);
        assertThat(response.courseCapacityMetrics().get(0).enrolledStudents()).isEqualTo(25);
        assertThat(response.courseCapacityMetrics().get(0).utilizationPercentage()).isEqualTo(50.0);

        assertThat(response.facultyWorkload()).hasSize(1);
        assertThat(response.facultyWorkload().get(0).facultyName()).isEqualTo("Dr. Alan Turing");
        assertThat(response.facultyWorkload().get(0).assignedCoursesCount()).isEqualTo(1);
    }
}
