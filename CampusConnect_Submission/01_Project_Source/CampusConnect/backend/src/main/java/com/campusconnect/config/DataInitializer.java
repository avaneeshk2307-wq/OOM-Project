package com.campusconnect.config;

import com.campusconnect.entity.*;
import com.campusconnect.enums.AssessmentType;
import com.campusconnect.enums.Role;
import com.campusconnect.enums.RoomType;
import com.campusconnect.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DataInitializer - Seeds initial university departments, demo accounts, and curriculum
 * when running dev/postgres profiles. Excluded from test profile to prevent unique key clashes.
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final DepartmentRepository departmentRepository;
    private final AdminRepository adminRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;
    private final TimetableRepository timetableRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            DepartmentRepository departmentRepository,
            AdminRepository adminRepository,
            FacultyRepository facultyRepository,
            StudentRepository studentRepository,
            UserAccountRepository userAccountRepository,
            CourseRepository courseRepository,
            ClassroomRepository classroomRepository,
            TimetableRepository timetableRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.departmentRepository = departmentRepository;
        this.adminRepository = adminRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.userAccountRepository = userAccountRepository;
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
        this.timetableRepository = timetableRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            log.info("Database already seeded with user accounts.");
            return;
        }

        log.info("Seeding initial university data into database...");

        // 1. Departments
        Department csDept = departmentRepository.save(new Department(
                "CS",
                "Computer Science & Engineering",
                "Department of Computer Science and Software Engineering"
        ));
        Department eeDept = departmentRepository.save(new Department(
                "EE",
                "Electrical & Electronics Engineering",
                "Department of Electrical Engineering"
        ));

        // 2. Admin Account
        Admin admin = adminRepository.save(new Admin(
                "System Administrator",
                "admin@campusconnect.edu",
                "+1-555-0100",
                "SUPER_ADMIN"
        ));
        userAccountRepository.save(new UserAccount(
                admin,
                "admin",
                passwordEncoder.encode("admin123"),
                Role.ROLE_ADMIN
        ));

        // 3. Faculty Account
        Faculty faculty = facultyRepository.save(new Faculty(
                "Dr. Alan Turing",
                "turing@campusconnect.edu",
                "+1-555-0101",
                "FAC-001",
                "Professor",
                csDept
        ));
        userAccountRepository.save(new UserAccount(
                faculty,
                "faculty",
                passwordEncoder.encode("faculty123"),
                Role.ROLE_FACULTY
        ));

        // 4. Student Account
        Student student = studentRepository.save(new Student(
                "John Doe",
                "john.doe@campusconnect.edu",
                "+1-555-0102",
                "CS2026-001",
                1,
                csDept
        ));
        userAccountRepository.save(new UserAccount(
                student,
                "student",
                passwordEncoder.encode("student123"),
                Role.ROLE_STUDENT
        ));

        // 5. Initial Course with Assessments
        Course oopCourse = new Course(
                "CS101",
                "Object-Oriented Programming with Java",
                "Fundamental concepts of OOP: Encapsulation, Inheritance, Abstraction, Polymorphism, and Design Patterns.",
                4,
                1,
                40,
                csDept,
                faculty
        );

        oopCourse.addAssessmentComponent(new AssessmentComponent(
                oopCourse,
                "Midterm Exam",
                AssessmentType.MIDTERM,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(30)
        ));

        oopCourse.addAssessmentComponent(new AssessmentComponent(
                oopCourse,
                "OOP Course Project",
                AssessmentType.PROJECT,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(30)
        ));

        oopCourse.addAssessmentComponent(new AssessmentComponent(
                oopCourse,
                "Final Theory Exam",
                AssessmentType.FINAL_EXAM,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(40)
        ));

        courseRepository.save(oopCourse);

        // 6. Classrooms & Timetable Slots
        Classroom lh101 = classroomRepository.save(new Classroom("LH-101", "Turing Science Complex", 60, RoomType.LECTURE_HALL));
        Classroom lab201 = classroomRepository.save(new Classroom("LAB-201", "Ada Lovelace Computing Wing", 40, RoomType.LABORATORY));
        classroomRepository.save(new Classroom("SEM-301", "von Neumann Hall", 50, RoomType.SEMINAR_ROOM));

        // Seed initial lecture sessions for CS101
        timetableRepository.save(new TimetableEntry(
                oopCourse,
                lh101,
                faculty,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 30)
        ));

        timetableRepository.save(new TimetableEntry(
                oopCourse,
                lab201,
                faculty,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(11, 0),
                LocalTime.of(12, 30)
        ));

        log.info("Database seeding completed successfully with Phase 2 Timetables & Classrooms.");
    }
}
