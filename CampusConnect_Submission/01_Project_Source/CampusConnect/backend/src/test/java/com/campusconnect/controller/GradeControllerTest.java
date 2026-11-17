package com.campusconnect.controller;

import com.campusconnect.dto.CreateAssessmentRequest;
import com.campusconnect.dto.SubmitGradeRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.AssessmentType;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GradeControllerTest {

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
    private AssessmentComponentRepository assessmentComponentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Course testCourse;
    private Student testStudent;
    private Faculty testFaculty;
    private Enrollment testEnrollment;

    @BeforeEach
    void setUp() {
        Department dept = departmentRepository.findByCode("TEST-GRD")
                .orElseGet(() -> departmentRepository.save(new Department("TEST-GRD", "Test Grading Dept", "Testing grades")));

        testFaculty = facultyRepository.findByEmployeeCode("GRD-FAC-01")
                .orElseGet(() -> facultyRepository.save(new Faculty("Dr. Grading", "dr.grd@campus.edu", "555-0801", "GRD-FAC-01", "Professor", dept)));

        userAccountRepository.findByUsername("dr_grd")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(testFaculty, "dr_grd", passwordEncoder.encode("secret"), Role.ROLE_FACULTY)));

        testStudent = studentRepository.findByRollNumber("GRD-STU-01")
                .orElseGet(() -> studentRepository.save(new Student("Bob Grader", "bob.grd@campus.edu", "555-0802", "GRD-STU-01", 2, dept)));

        userAccountRepository.findByUsername("bob_grd")
                .orElseGet(() -> userAccountRepository.save(new UserAccount(testStudent, "bob_grd", passwordEncoder.encode("secret"), Role.ROLE_STUDENT)));

        testCourse = courseRepository.findByCode("GRD101")
                .orElseGet(() -> courseRepository.save(new Course("GRD101", "Object Oriented Design", "OOD", 4, 2, 50, dept, testFaculty)));

        // Enroll Bob to ENROLLED state
        testEnrollment = new Enrollment(testStudent, testCourse, 2);
        testEnrollment.submit();
        testEnrollment.approve();
        testEnrollment.finalizeEnrollment();
        testEnrollment = enrollmentRepository.save(testEnrollment);
    }

    @Test
    @WithMockUser(username = "dr_grd", authorities = {"ROLE_FACULTY"})
    void testCreateAssessmentAndSubmitGradeWithStrategyPattern() throws Exception {
        // 1. Create Assessment Component
        CreateAssessmentRequest assessmentReq = new CreateAssessmentRequest(
                testCourse.getId(),
                "Midterm Exam",
                AssessmentType.MIDTERM,
                BigDecimal.valueOf(100.0),
                BigDecimal.valueOf(40.0)
        );

        String assessmentResStr = mockMvc.perform(post("/api/v1/grades/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assessmentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Midterm Exam"))
                .andExpect(jsonPath("$.maxMarks").value(100.0))
                .andReturn().getResponse().getContentAsString();

        Long assessmentId = objectMapper.readTree(assessmentResStr).get("id").asLong();

        // 2. Submit Grade: Bob scores 85 out of 100 -> Absolute Strategy: Grade A (4.0 points)
        SubmitGradeRequest gradeReq = new SubmitGradeRequest(
                testEnrollment.getId(),
                assessmentId,
                BigDecimal.valueOf(85.0)
        );

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marksObtained").value(85.0))
                .andExpect(jsonPath("$.letterGrade").value("A"))
                .andExpect(jsonPath("$.gradePoint").value(4.0))
                .andExpect(jsonPath("$.remark").value("Excellent"));
    }

    @Test
    @WithMockUser(username = "bob_grd", authorities = {"ROLE_STUDENT"})
    void testStudentViewGradeReport() throws Exception {
        // Create an assessment and a grade directly in domain
        AssessmentComponent assessment = new AssessmentComponent(
                testCourse,
                "Final Project",
                AssessmentType.PROJECT,
                BigDecimal.valueOf(100.0),
                BigDecimal.valueOf(100.0)
        );
        testCourse.addAssessmentComponent(assessment);
        assessmentComponentRepository.save(assessment);

        SubmitGradeRequest gradeReq = new SubmitGradeRequest(
                testEnrollment.getId(),
                assessment.getId(),
                BigDecimal.valueOf(92.0)
        );

        // Grade via student's report endpoint
        mockMvc.perform(get("/api/v1/grades/my-grades"))
                .andExpect(status().isOk());
    }
}
