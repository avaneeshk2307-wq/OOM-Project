package com.campusconnect.controller;

import com.campusconnect.dto.CreateSessionRequest;
import com.campusconnect.dto.RecordEntryRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.AttendanceStatus;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private AttendanceSessionRepository attendanceSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Course testCourse;
    private Student testStudent;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("TEST-ATT")
                .orElseGet(() -> departmentRepository.save(new Department("TEST-ATT", "Test Attendance Dept", "Dept for testing")));

        testFaculty = facultyRepository.findByEmployeeCode("ATT-FAC-01")
                .orElseGet(() -> facultyRepository.save(new Faculty("Prof. Attendance", "prof.att@campus.edu", "555-0901", "ATT-FAC-01", "Professor", dept)));

        userAccountRepository.findByUsername("prof_att")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(testFaculty, "prof_att", passwordEncoder.encode("secret"), Role.ROLE_FACULTY)));

        testStudent = studentRepository.findByRollNumber("ATT-STU-01")
                .orElseGet(() -> studentRepository.save(new Student("Alice Attendant", "alice.att@campus.edu", "555-0902", "ATT-STU-01", 3, dept)));

        userAccountRepository.findByUsername("alice_att")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(testStudent, "alice_att", passwordEncoder.encode("secret"), Role.ROLE_STUDENT)));

        testCourse = courseRepository.findByCode("ATT101")
                .orElseGet(() -> courseRepository.save(new Course("ATT101", "Software Testing", "Desc", 4, 3, 60, dept, testFaculty)));

        // Enroll student to ENROLLED state
        Enrollment enrollment = new Enrollment(testStudent, testCourse, 3);
        enrollment.submit();
        enrollment.approve();
        enrollment.finalizeEnrollment();
        enrollmentRepository.save(enrollment);
    }

    @Test
    @WithMockUser(username = "prof_att", authorities = {"ROLE_FACULTY"})
    void testCreateSessionAndRecordAttendance() throws Exception {
        RecordEntryRequest record = new RecordEntryRequest(testStudent.getId(), AttendanceStatus.PRESENT, "Participated actively");
        CreateSessionRequest request = new CreateSessionRequest(
                testCourse.getId(),
                LocalDate.now(),
                "Unit Testing Fundamentals",
                List.of(record)
        );

        mockMvc.perform(post("/api/v1/attendance/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseCode").value("ATT101"))
                .andExpect(jsonPath("$.topic").value("Unit Testing Fundamentals"))
                .andExpect(jsonPath("$.totalRecords").value(1))
                .andExpect(jsonPath("$.presentCount").value(1))
                .andExpect(jsonPath("$.records[0].status").value("PRESENT"));
    }

    @Test
    @WithMockUser(username = "alice_att", authorities = {"ROLE_STUDENT"})
    void testStudentAttendanceSummaryWithLowAttendanceWarning() throws Exception {
        // Create 1 session with ABSENT record via composition root
        AttendanceSession session = new AttendanceSession(testCourse, LocalDate.now().minusDays(2), "Session 1");
        session.recordStudentAttendance(testStudent, AttendanceStatus.ABSENT, "Medical");
        attendanceSessionRepository.save(session);

        // Alice views her attendance
        mockMvc.perform(get("/api/v1/attendance/my-attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseCode").value("ATT101"))
                .andExpect(jsonPath("$[0].totalSessions").value(1))
                .andExpect(jsonPath("$[0].attendedSessions").value(0))
                .andExpect(jsonPath("$[0].percentage").value(0.0))
                .andExpect(jsonPath("$[0].lowAttendanceWarning").value(true));
    }
}
