package com.campusconnect.repository;

import com.campusconnect.entity.*;
import com.campusconnect.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DomainModelAndRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private AttendanceSessionRepository attendanceSessionRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private GradeRepository gradeRepository;

    private Department csDept;

    @BeforeEach
    void setUp() {
        csDept = departmentRepository.findByCode("CS")
                .orElseGet(() -> departmentRepository.save(new Department("CS", "Computer Science", "Department of CS")));
    }

    @Test
    @DisplayName("OOM Inheritance: Student & Faculty inherit from Person using JOINED strategy")
    void testJoinedInheritanceHierarchy() {
        Student student = new Student("Alice Walker", "alice@university.edu", "555-0101", "CS2026-001", 1, csDept);
        Student savedStudent = studentRepository.save(student);

        Faculty faculty = new Faculty("Dr. Robert Smith", "robert@university.edu", "555-0102", "FAC-001", "Professor", csDept);
        Faculty savedFaculty = facultyRepository.save(faculty);

        // Polymorphic query via Person superclass repository
        Optional<Person> personStudent = personRepository.findById(savedStudent.getId());
        Optional<Person> personFaculty = personRepository.findById(savedFaculty.getId());

        assertThat(personStudent).isPresent();
        assertThat(personStudent.get()).isInstanceOf(Student.class);
        assertThat(((Student) personStudent.get()).getRollNumber()).isEqualTo("CS2026-001");

        assertThat(personFaculty).isPresent();
        assertThat(personFaculty.get()).isInstanceOf(Faculty.class);
        assertThat(((Faculty) personFaculty.get()).getEmployeeCode()).isEqualTo("FAC-001");
    }

    @Test
    @DisplayName("OOM Composition: Course owns AssessmentComponents with cascade persistence")
    void testCourseAssessmentComposition() {
        Course course = new Course("CS101", "Introduction to Programming", "Core programming course", 4, 1, 60, csDept, null);
        AssessmentComponent midterm = new AssessmentComponent(course, "Midterm Exam", AssessmentType.MIDTERM, BigDecimal.valueOf(100), BigDecimal.valueOf(30));
        AssessmentComponent finalExam = new AssessmentComponent(course, "Final Exam", AssessmentType.FINAL_EXAM, BigDecimal.valueOf(100), BigDecimal.valueOf(50));

        course.addAssessmentComponent(midterm);
        course.addAssessmentComponent(finalExam);

        Course savedCourse = courseRepository.save(course);

        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getAssessmentComponents()).hasSize(2);
        assertThat(savedCourse.getAssessmentComponents().get(0).getId()).isNotNull();
    }

    @Test
    @DisplayName("OOM Encapsulation & State Invariants: Enrollment state transitions")
    void testEnrollmentLifecycleTransitions() {
        Student student = studentRepository.save(new Student("Bob Jones", "bob@university.edu", "555-0202", "CS2026-002", 1, csDept));
        Course course = courseRepository.save(new Course("CS102", "Data Structures", "Core DS", 4, 1, 40, csDept, null));

        Enrollment enrollment = new Enrollment(student, course, 1);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DRAFT);

        // DRAFT -> SUBMITTED
        enrollment.submit();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.SUBMITTED);

        // Disallow direct finalize to ENROLLED without APPROVAL
        assertThatThrownBy(enrollment::finalizeEnrollment)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only APPROVED enrollments");

        // SUBMITTED -> APPROVED -> ENROLLED
        enrollment.approve();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);

        enrollment.finalizeEnrollment();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);

        // ENROLLED -> DROPPED
        enrollment.drop();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);

        // Dropped cannot transition to SUBMITTED
        assertThatThrownBy(enrollment::submit)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("OOM Encapsulation: Grade marks validation against assessment maximum")
    void testGradeInvariantsValidation() {
        Student student = studentRepository.save(new Student("Charlie Ray", "charlie@university.edu", "555-0303", "CS2026-003", 1, csDept));
        Course course = new Course("CS103", "Database Systems", "DBMS Fundamentals", 3, 2, 50, csDept, null);
        AssessmentComponent quiz = new AssessmentComponent(course, "Quiz 1", AssessmentType.QUIZ, BigDecimal.valueOf(20), BigDecimal.valueOf(10));
        course.addAssessmentComponent(quiz);
        Course savedCourse = courseRepository.save(course);

        Enrollment enrollment = enrollmentRepository.save(new Enrollment(student, savedCourse, 2));

        // Negative marks must throw IllegalArgumentException
        assertThatThrownBy(() -> new Grade(enrollment, quiz, BigDecimal.valueOf(-5), "F"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");

        // Marks exceeding max (25 > 20) must throw IllegalArgumentException
        assertThatThrownBy(() -> new Grade(enrollment, quiz, BigDecimal.valueOf(25), "A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed assessment maximum");

        // Valid marks
        Grade validGrade = gradeRepository.save(new Grade(enrollment, quiz, BigDecimal.valueOf(18), "A"));
        assertThat(validGrade.getId()).isNotNull();
        assertThat(validGrade.getMarksObtained()).isEqualByComparingTo(BigDecimal.valueOf(18));
    }

    @Test
    @DisplayName("OOM Composition: AttendanceSession manages AttendanceRecords and calculates present count")
    void testAttendanceSessionManagement() {
        Student student1 = studentRepository.save(new Student("Student One", "s1@university.edu", "555-1111", "CS2026-011", 1, csDept));
        Student student2 = studentRepository.save(new Student("Student Two", "s2@university.edu", "555-2222", "CS2026-022", 1, csDept));
        Course course = courseRepository.save(new Course("CS104", "Operating Systems", "OS Concepts", 4, 3, 40, csDept, null));

        AttendanceSession session = new AttendanceSession(course, LocalDate.now(), "Process Synchronization");
        session.recordStudentAttendance(student1, AttendanceStatus.PRESENT, "On time");
        session.recordStudentAttendance(student2, AttendanceStatus.ABSENT, "Unexcused");

        AttendanceSession savedSession = attendanceSessionRepository.save(session);

        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getRecords()).hasSize(2);
        assertThat(savedSession.getPresentCount()).isEqualTo(1);

        // Dynamic aggregate repository calculations
        long totalSessions = attendanceRecordRepository.countTotalByCourseAndStudent(course.getId(), student1.getId());
        long attendedSessions = attendanceRecordRepository.countAttendedByCourseAndStudent(course.getId(), student1.getId());

        assertThat(totalSessions).isEqualTo(1);
        assertThat(attendedSessions).isEqualTo(1);
    }
}
