package com.campusconnect.controller;

import com.campusconnect.dto.LoginRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.Role;
import com.campusconnect.enums.RoomType;
import com.campusconnect.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TimetableTimeParsingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Long testCourseId;
    private Long testClassroomId;
    private Long testFacultyId;

    @BeforeEach
    void setUp() throws Exception {
        if (!userAccountRepository.existsByUsername("admin_time_test")) {
            Admin admin = adminRepository.save(new Admin("Time Admin", "time_admin@campusconnect.edu", "555-001", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin_time_test", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        LoginRequest req = new LoginRequest("admin_time_test", "admin123");
        String res = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(res).get("token").asText();

        Department dept = departmentRepository.findByCode("TIME_DEPT")
                .orElseGet(() -> departmentRepository.save(new Department("TIME_DEPT", "Time Dept", "Desc")));

        Faculty faculty = facultyRepository.findByEmployeeCode("FAC_TIME_01")
                .orElseGet(() -> facultyRepository.save(new Faculty("Prof. Time", "prof.time@campusconnect.edu", "555-002", "FAC_TIME_01", "Prof", dept)));
        testFacultyId = faculty.getId();

        Course course = courseRepository.findByCode("TIME101")
                .orElseGet(() -> courseRepository.save(new Course("TIME101", "Time Analysis", "Desc", 3, 1, 30, dept, faculty)));
        testCourseId = course.getId();

        Classroom classroom = classroomRepository.findByRoomNumber("ROOM_TIME_01")
                .orElseGet(() -> classroomRepository.save(new Classroom("ROOM_TIME_01", "Science Hall", 50, RoomType.LECTURE_HALL)));
        testClassroomId = classroom.getId();
    }

    @Test
    @DisplayName("BUG-05: Valid HH:mm time input is accepted (201)")
    void testValidHhMmTimeAccepted() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "startTime": "08:00",
                "endTime": "09:30"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime").value("08:00"))
                .andExpect(jsonPath("$.endTime").value("09:30"));
    }

    @Test
    @DisplayName("BUG-05: Valid HH:mm:ss time input is accepted (201)")
    void testValidHhMmSsTimeAccepted() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "startTime": "10:00:00",
                "endTime": "11:30:00"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime").value("10:00"))
                .andExpect(jsonPath("$.endTime").value("11:30"));
    }

    @Test
    @DisplayName("BUG-05: Invalid time string produces HTTP 400 (not 500)")
    void testInvalidTimeFormatReturns400() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "startTime": "25:99",
                "endTime": "12:00"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("BUG-05: Empty time string produces HTTP 400 (not 500)")
    void testEmptyTimeReturns400() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "startTime": "",
                "endTime": "12:00"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("BUG-05: Missing time field produces HTTP 400")
    void testMissingTimeReturns400() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "endTime": "12:00"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("BUG-05: Start time later than end time produces HTTP 400")
    void testStartTimeAfterEndTimeReturns400() throws Exception {
        String json = String.format("""
            {
                "courseId": %d,
                "classroomId": %d,
                "facultyId": %d,
                "dayOfWeek": "FRIDAY",
                "startTime": "14:00",
                "endTime": "13:00"
            }
            """, testCourseId, testClassroomId, testFacultyId);

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Start time (14:00) must be strictly before end time (13:00)"));
    }
}
