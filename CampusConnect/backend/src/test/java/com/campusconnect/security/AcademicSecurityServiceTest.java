package com.campusconnect.security;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AcademicSecurityServiceTest {

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
    private AdminRepository adminRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String facultyAssignedToken;
    private String facultyUnassignedToken;
    private String studentAToken;
    private String studentBToken;

    private Long studentAId;
    private Long studentBId;
    private Long courseTaughtId;

    @BeforeEach
    void setUpTestData() throws Exception {
        Department dept = departmentRepository.findByCode("SEC_DEPT")
                .orElseGet(() -> departmentRepository.save(new Department("SEC_DEPT", "Security Department", "Desc")));

        // 1. Admin setup
        if (!userAccountRepository.existsByUsername("sec_admin")) {
            Admin admin = adminRepository.save(new Admin("Sec Admin", "sec_admin@campusconnect.edu", "555-001", "SUPER_ADMIN"));
            userAccountRepository.save(new UserAccount(admin, "sec_admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        }
        adminToken = loginAndGetToken("sec_admin", "admin123");

        // 2. Faculty Assigned (teaches courseTaught)
        Faculty facultyAssigned = facultyRepository.findByEmployeeCode("FAC_SEC_01")
                .orElseGet(() -> facultyRepository.save(new Faculty("Prof. Assigned", "fac1@campusconnect.edu", "555-002", "FAC_SEC_01", "Prof", dept)));
        if (!userAccountRepository.existsByUsername("fac_assigned")) {
            userAccountRepository.save(new UserAccount(facultyAssigned, "fac_assigned", passwordEncoder.encode("faculty123"), Role.ROLE_FACULTY));
        }
        facultyAssignedToken = loginAndGetToken("fac_assigned", "faculty123");

        // 3. Faculty Unassigned (does not teach courseTaught)
        Faculty facultyUnassigned = facultyRepository.findByEmployeeCode("FAC_SEC_02")
                .orElseGet(() -> facultyRepository.save(new Faculty("Prof. Unassigned", "fac2@campusconnect.edu", "555-003", "FAC_SEC_02", "Prof", dept)));
        if (!userAccountRepository.existsByUsername("fac_unassigned")) {
            userAccountRepository.save(new UserAccount(facultyUnassigned, "fac_unassigned", passwordEncoder.encode("faculty123"), Role.ROLE_FACULTY));
        }
        facultyUnassignedToken = loginAndGetToken("fac_unassigned", "faculty123");

        // 4. Course taught by facultyAssigned
        Course course = courseRepository.findByCode("SEC101")
                .orElseGet(() -> courseRepository.save(new Course("SEC101", "Security Engineering", "Desc", 3, 1, 40, dept, facultyAssigned)));
        courseTaughtId = course.getId();

        // 5. Student A (enrolled in SEC101 taught by facultyAssigned)
        Student studentA = studentRepository.findByRollNumber("SEC_STU_A")
                .orElseGet(() -> studentRepository.save(new Student("Student A", "stu_a@campusconnect.edu", "555-004", "SEC_STU_A", 1, dept)));
        studentAId = studentA.getId();
        if (!userAccountRepository.existsByUsername("stu_a")) {
            userAccountRepository.save(new UserAccount(studentA, "stu_a", passwordEncoder.encode("student123"), Role.ROLE_STUDENT));
        }
        studentAToken = loginAndGetToken("stu_a", "student123");

        // Enroll Student A in SEC101
        if (!enrollmentRepository.existsByStudentIdAndCourseIdAndSemester(studentAId, courseTaughtId, 1)) {
            enrollmentRepository.save(new Enrollment(studentA, course, 1));
        }

        // 6. Student B (not in SEC101)
        Student studentB = studentRepository.findByRollNumber("SEC_STU_B")
                .orElseGet(() -> studentRepository.save(new Student("Student B", "stu_b@campusconnect.edu", "555-005", "SEC_STU_B", 1, dept)));
        studentBId = studentB.getId();
        if (!userAccountRepository.existsByUsername("stu_b")) {
            userAccountRepository.save(new UserAccount(studentB, "stu_b", passwordEncoder.encode("student123"), Role.ROLE_STUDENT));
        }
        studentBToken = loginAndGetToken("stu_b", "student123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest req = new LoginRequest(username, password);
        String res = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("token").asText();
    }

    @Test
    @DisplayName("IDOR Prevention: Admin is allowed to access any student academic records")
    void testAdminCanAccessAnyStudentRecord() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IDOR Prevention: Student is allowed to access own records")
    void testStudentCanAccessOwnRecords() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentAToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentAToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentAToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IDOR Prevention: Student B is forbidden (403) from accessing Student A records")
    void testStudentCannotAccessOtherStudentRecord() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentBToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentBToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId)
                        .header("Authorization", "Bearer " + studentBToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("IDOR Prevention: Faculty teaching student is allowed to access student records")
    void testFacultyTeachingStudentAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyAssignedToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyAssignedToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyAssignedToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IDOR Prevention: Faculty NOT teaching student is forbidden (403)")
    void testFacultyNotTeachingStudentForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyUnassignedToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyUnassignedToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId)
                        .header("Authorization", "Bearer " + facultyUnassignedToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("IDOR Prevention: Unauthenticated request is rejected with 401")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/student/" + studentAId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/attendance/student/" + studentAId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/grades/student/" + studentAId))
                .andExpect(status().isUnauthorized());
    }
}
