package com.campusconnect.controller;

import com.campusconnect.dto.CreateAnnouncementRequest;
import com.campusconnect.entity.Admin;
import com.campusconnect.entity.Student;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.AdminRepository;
import com.campusconnect.repository.DepartmentRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.UserAccountRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("TEST-ANN")
                .orElseGet(() -> departmentRepository.save(new Department("TEST-ANN", "Test Announcement Dept", "Testing announcements")));

        Admin admin = adminRepository.save(new Admin("Admin Announcer", "admin.ann@campus.edu", "555-0701", "CHIEF"));
        userAccountRepository.findByUsername("admin_ann")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(admin, "admin_ann", passwordEncoder.encode("secret"), Role.ROLE_ADMIN)));

        Student student = studentRepository.save(new Student("Student Reader", "reader@campus.edu", "555-0702", "ANN-STU-01", 1, dept));
        userAccountRepository.findByUsername("student_ann")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(student, "student_ann", passwordEncoder.encode("secret"), Role.ROLE_STUDENT)));
    }

    @Test
    @WithMockUser(username = "admin_ann", authorities = {"ROLE_ADMIN"})
    void testCreateAndDeleteAnnouncement() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest(
                "Campus Maintenance Notice",
                "Library will remain closed this Sunday.",
                null,
                null
        );

        String res = mockMvc.perform(post("/api/v1/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Campus Maintenance Notice"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(res).get("id").asLong();

        mockMvc.perform(delete("/api/v1/announcements/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Announcement deleted successfully"));
    }

    @Test
    @WithMockUser(username = "student_ann", authorities = {"ROLE_STUDENT"})
    void testRoleBasedAnnouncementFiltering() throws Exception {
        mockMvc.perform(get("/api/v1/announcements"))
                .andExpect(status().isOk());
    }
}
