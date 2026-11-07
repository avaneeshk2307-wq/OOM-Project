package com.campusconnect.controller;

import com.campusconnect.dto.LoginRequest;
import com.campusconnect.entity.Admin;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.Student;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.AdminRepository;
import com.campusconnect.repository.DepartmentRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.UserAccountRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditAndSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUpAccounts() throws Exception {
        if (!userAccountRepository.existsByUsername("audit_admin")) {
            Admin admin = adminRepository.save(new Admin("Audit Admin", "audit.admin@campus.edu", "555-1111", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "audit_admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        if (!userAccountRepository.existsByUsername("audit_student")) {
            Department dept = departmentRepository.save(new Department("SEC", "Security Dept", "Sec Department"));
            Student student = studentRepository.save(new Student("Sec Student", "sec.student@campus.edu", "555-2222", "SEC2026-001", 1, dept));
            userAccountRepository.save(new UserAccount(student, "audit_student", passwordEncoder.encode("student123"), Role.ROLE_STUDENT));
        }

        // Admin Token
        String adminRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("audit_admin", "admin123"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminRes).get("token").asText();

        // Student Token
        String studentRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("audit_student", "student123"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        studentToken = objectMapper.readTree(studentRes).get("token").asText();
    }

    @Test
    @DisplayName("Admin should be authorized to read audit logs and analytics")
    void testAdminAccessToAuditAndAnalytics() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        mockMvc.perform(get("/api/v1/analytics/overview")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentMetrics").isArray())
                .andExpect(jsonPath("$.attendanceHealth").isNotEmpty());
    }

    @Test
    @DisplayName("Student role should be forbidden (403) from administrative audit logs and analytics")
    void testStudentForbiddenFromAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/analytics/overview")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated client should receive 401 Unauthorized")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isUnauthorized());
    }
}
