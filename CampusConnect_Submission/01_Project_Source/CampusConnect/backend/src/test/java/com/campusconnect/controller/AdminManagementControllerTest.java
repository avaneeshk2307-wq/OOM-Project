package com.campusconnect.controller;

import com.campusconnect.dto.*;
import com.campusconnect.entity.Admin;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.AdminRepository;
import com.campusconnect.repository.UserAccountRepository;
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
class AdminManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUpAdminUser() throws Exception {
        if (!userAccountRepository.existsByUsername("admin")) {
            Admin admin = adminRepository.save(new Admin("Admin User", "admin.mgt@campusconnect.edu", "555-0900", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        adminToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Admin CRUD workflow: Department, Course, Faculty, Student creation and Student Promotion")
    void testCompleteAdminManagementWorkflow() throws Exception {
        // 1. Create Department
        DepartmentRequest deptReq = new DepartmentRequest("MATH", "Mathematics & Computing", "Department of Mathematics");
        String deptRes = mockMvc.perform(post("/api/v1/admin/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deptReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("MATH"))
                .andReturn().getResponse().getContentAsString();

        Long deptId = objectMapper.readTree(deptRes).get("id").asLong();

        // 2. Create Faculty
        CreateFacultyRequest facultyReq = new CreateFacultyRequest(
                "Prof. Ada Lovelace",
                "ada@campusconnect.edu",
                "555-0808",
                "FAC-088",
                "Associate Professor",
                deptId,
                "password123"
        );
        String facultyRes = mockMvc.perform(post("/api/v1/admin/faculty")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facultyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value("FAC-088"))
                .andReturn().getResponse().getContentAsString();

        Long facultyId = objectMapper.readTree(facultyRes).get("id").asLong();

        // 3. Create Course assigned to Faculty
        CourseRequest courseReq = new CourseRequest(
                "MTH101",
                "Calculus & Linear Algebra",
                "Foundational Math for Engineers",
                4,
                1,
                50,
                deptId,
                facultyId
        );
        mockMvc.perform(post("/api/v1/admin/courses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("MTH101"))
                .andExpect(jsonPath("$.instructorName").value("Prof. Ada Lovelace"));

        // 4. Create Student
        CreateStudentRequest studentReq = new CreateStudentRequest(
                "David Miller",
                "david@campusconnect.edu",
                "555-0707",
                "MATH2026-001",
                1,
                deptId,
                "password123"
        );
        String studentRes = mockMvc.perform(post("/api/v1/admin/students")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rollNumber").value("MATH2026-001"))
                .andExpect(jsonPath("$.semester").value(1))
                .andReturn().getResponse().getContentAsString();

        Long studentId = objectMapper.readTree(studentRes).get("id").asLong();

        // 5. Promote Student to next semester (OOM Encapsulation)
        mockMvc.perform(post("/api/v1/admin/students/" + studentId + "/promote")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.semester").value(2));

        // 6. Verify System Statistics
        mockMvc.perform(get("/api/v1/admin/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDepartments").isNumber())
                .andExpect(jsonPath("$.totalCourses").isNumber())
                .andExpect(jsonPath("$.totalStudents").isNumber());
    }

    @Test
    @DisplayName("Non-admin request to admin endpoints should be forbidden")
    void testNonAdminAccessForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/departments"))
                .andExpect(status().isUnauthorized());
    }
}
