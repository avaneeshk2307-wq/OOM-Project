package com.campusconnect.controller;

import com.campusconnect.dto.CreateTimetableRequest;
import com.campusconnect.dto.LoginRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.Role;
import com.campusconnect.enums.RoomType;
import com.campusconnect.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TimetableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Long courseId;
    private Long classroomId;
    private Long facultyId;

    @BeforeEach
    void setUpTestData() throws Exception {
        if (!userAccountRepository.existsByUsername("admin_tt")) {
            Admin admin = adminRepository.save(new Admin("TT Admin", "admin.tt@campusconnect.edu", "555-9999", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin_tt", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        LoginRequest loginRequest = new LoginRequest("admin_tt", "admin123");
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        adminToken = root.get("token").asText();

        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Department dept = departmentRepository.save(new Department("D" + suffix, "Timetable Dept " + suffix, "Timetable Department"));
        Faculty faculty = facultyRepository.save(new Faculty("Prof. Turing " + suffix, "tt." + suffix.toLowerCase() + "@campus.edu", "555-8888", "F-" + suffix, "Professor", dept));
        Course course = courseRepository.save(new Course("C" + suffix, "Algorithms " + suffix, "Intro to Algo", 4, 1, 40, dept, faculty));
        Classroom classroom = classroomRepository.save(new Classroom("R-" + suffix, "Main Block", 50, RoomType.LECTURE_HALL));

        this.courseId = course.getId();
        this.facultyId = faculty.getId();
        this.classroomId = classroom.getId();
    }

    @Test
    @DisplayName("Should schedule timetable entry and detect room conflicts returning 409 Conflict")
    void testScheduleAndConflictDetection() throws Exception {
        // 1. First Schedule: Monday 10:00 to 11:30
        CreateTimetableRequest req1 = new CreateTimetableRequest(
                courseId,
                classroomId,
                facultyId,
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 30)
        );

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").isNotEmpty());

        // 2. Overlapping Schedule in same room: Monday 11:00 to 12:30 -> Conflict!
        CreateTimetableRequest req2 = new CreateTimetableRequest(
                courseId,
                classroomId,
                facultyId,
                DayOfWeek.MONDAY,
                LocalTime.of(11, 0),
                LocalTime.of(12, 30)
        );

        mockMvc.perform(post("/api/v1/timetable")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Should block unauthenticated access to timetable endpoints with 401")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/timetable/my-timetable"))
                .andExpect(status().isUnauthorized());
    }
}
