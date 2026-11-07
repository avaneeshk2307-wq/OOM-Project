package com.campusconnect.controller;

import com.campusconnect.dto.EnrollmentRegisterRequest;
import com.campusconnect.dto.LoginRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.Role;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnrollmentControllerTest {

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
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;
    private Long testCourseId;
    private Long testStudentId;

    @BeforeEach
    void setUpTestData() throws Exception {
        // Admin user setup
        if (!userAccountRepository.existsByUsername("admin")) {
            Admin admin = adminRepository.save(new Admin("Admin User", "admin.enroll@campusconnect.edu", "555-0900", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        LoginRequest adminLogin = new LoginRequest("admin", "admin123");
        String adminRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminRes).get("token").asText();

        // Department & Course
        Department dept = departmentRepository.findByCode("TEST_DEPT")
                .orElseGet(() -> departmentRepository.save(new Department("TEST_DEPT", "Test Dept", "Desc")));

        Course course = courseRepository.findByCode("TEST101")
                .orElseGet(() -> courseRepository.save(new Course("TEST101", "Testing 101", "Desc", 3, 1, 30, dept, null)));
        testCourseId = course.getId();

        // Student user setup
        if (!userAccountRepository.existsByUsername("test_student")) {
            Student student = studentRepository.save(new Student("Test Student", "student.test@campusconnect.edu", "555-1234", "TEST-ROLL-99", 1, dept));
            userAccountRepository.save(new UserAccount(student, "test_student", passwordEncoder.encode("student123"), Role.ROLE_STUDENT));
            testStudentId = student.getId();
        } else {
            testStudentId = userAccountRepository.findByUsername("test_student").get().getPerson().getId();
        }

        LoginRequest studentLogin = new LoginRequest("test_student", "student123");
        String studentRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        studentToken = objectMapper.readTree(studentRes).get("token").asText();
    }

    @Test
    @DisplayName("State Pattern & Enrollment Workflow: Register -> Approve -> Finalize -> Drop & Duplicate Prevention")
    void testEnrollmentLifecycleAndStatePattern() throws Exception {
        EnrollmentRegisterRequest req = new EnrollmentRegisterRequest(testCourseId, 1);

        // 1. Student registers for course -> Transitions to SUBMITTED
        String regRes = mockMvc.perform(post("/api/v1/enrollments/register")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.courseCode").value("TEST101"))
                .andReturn().getResponse().getContentAsString();

        Long enrollmentId = objectMapper.readTree(regRes).get("id").asLong();

        // 2. Duplicate registration must fail
        mockMvc.perform(post("/api/v1/enrollments/register")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());

        // 3. Admin approves enrollment: SUBMITTED -> APPROVED
        mockMvc.perform(put("/api/v1/enrollments/" + enrollmentId + "/approve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        // 4. Admin finalizes enrollment: APPROVED -> ENROLLED
        mockMvc.perform(put("/api/v1/enrollments/" + enrollmentId + "/finalize")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENROLLED"));

        // 5. Student drops enrollment: ENROLLED -> DROPPED
        mockMvc.perform(put("/api/v1/enrollments/" + enrollmentId + "/drop")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DROPPED"));

        // 6. Illegal State Transition: Dropped cannot be finalized again
        mockMvc.perform(put("/api/v1/enrollments/" + enrollmentId + "/finalize")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }
}
