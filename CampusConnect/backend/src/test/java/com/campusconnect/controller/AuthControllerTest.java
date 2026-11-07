package com.campusconnect.controller;

import com.campusconnect.dto.LoginRequest;
import com.campusconnect.entity.Admin;
import com.campusconnect.entity.Faculty;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.AdminRepository;
import com.campusconnect.repository.FacultyRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

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
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpTestData() {
        if (!userAccountRepository.existsByUsername("admin")) {
            Admin admin = adminRepository.save(new Admin("Admin User", "admin.test@campusconnect.edu", "555-0001", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }

        if (!userAccountRepository.existsByUsername("faculty")) {
            Faculty faculty = facultyRepository.save(new Faculty("Faculty User", "faculty.test@campusconnect.edu", "555-0002", "FAC-001", "Professor", null));
            userAccountRepository.save(new UserAccount(faculty, "faculty", passwordEncoder.encode("faculty123"), Role.ROLE_FACULTY));
        }
    }

    @Test
    @DisplayName("POST /api/v1/auth/login with valid admin credentials should return JWT and user metadata")
    void testValidLogin() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("token");
    }

    @Test
    @DisplayName("POST /api/v1/auth/login with invalid password should fail authentication")
    void testInvalidLogin() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/auth/me should succeed with valid JWT bearer token and fail without it")
    void testCurrentUserProfile() throws Exception {
        // 1. Without token -> 401 Unauthorized
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());

        // 2. Perform login to get token
        LoginRequest request = new LoginRequest("faculty", "faculty123");
        String responseContent = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseContent).get("token").asText();

        // 3. With token -> 200 OK
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("faculty"))
                .andExpect(jsonPath("$.role").value("ROLE_FACULTY"))
                .andExpect(jsonPath("$.roleSpecificCode").value("FAC-001"));
    }
}
