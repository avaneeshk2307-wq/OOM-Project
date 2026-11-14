package com.campusconnect.controller;

import com.campusconnect.dto.LoginRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUpAdmin() throws Exception {
        if (!userAccountRepository.existsByUsername("admin_page_test")) {
            Admin admin = adminRepository.save(new Admin("Page Admin", "page_admin@campusconnect.edu", "555-001", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin_page_test", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        LoginRequest req = new LoginRequest("admin_page_test", "admin123");
        String res = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(res).get("token").asText();
    }

    @Test
    @DisplayName("BUG-07: Admin students endpoint returns Spring Data Page with pagination metadata")
    void testStudentsPagination() throws Exception {
        mockMvc.perform(get("/api/v1/admin/students")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("BUG-07: Admin faculty endpoint returns Spring Data Page with pagination metadata")
    void testFacultyPagination() throws Exception {
        mockMvc.perform(get("/api/v1/admin/faculty")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("BUG-07: Admin courses endpoint returns Spring Data Page with pagination metadata")
    void testCoursesPagination() throws Exception {
        mockMvc.perform(get("/api/v1/admin/courses")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "code,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("BUG-07: Enrollments endpoint returns Spring Data Page with pagination metadata")
    void testEnrollmentsPagination() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }
}
