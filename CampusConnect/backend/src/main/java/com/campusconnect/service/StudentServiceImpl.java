package com.campusconnect.service;

import com.campusconnect.dto.CreateStudentRequest;
import com.campusconnect.dto.StudentResponse;
import com.campusconnect.dto.UpdateStudentRequest;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.Student;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.AuditAction;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.DepartmentRepository;
import com.campusconnect.repository.PersonRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            PersonRepository personRepository,
            DepartmentRepository departmentRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService
    ) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (personRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new IllegalArgumentException("User with email '" + request.email() + "' already exists.");
        }
        if (studentRepository.existsByRollNumber(request.rollNumber().trim().toUpperCase())) {
            throw new IllegalArgumentException("Student with roll number '" + request.rollNumber() + "' already exists.");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.departmentId()));

        Student student = new Student(
                request.name(),
                request.email(),
                request.phone(),
                request.rollNumber(),
                request.semester(),
                department
        );

        Student savedStudent = studentRepository.save(student);

        // Derive username from email prefix or rollNumber
        String username = request.email().split("@")[0].toLowerCase();
        if (userAccountRepository.existsByUsername(username)) {
            username = request.rollNumber().toLowerCase().replace("-", "");
        }

        UserAccount account = new UserAccount(
                savedStudent,
                username,
                passwordEncoder.encode(request.initialPassword()),
                Role.ROLE_STUDENT
        );
        userAccountRepository.save(account);

        auditLogService.recordLog(
                AuditAction.CREATE_STUDENT,
                "Student",
                savedStudent.getId().toString(),
                "Created student roll: " + savedStudent.getRollNumber() + ", name: " + savedStudent.getName()
        );

        return mapToResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
        return mapToResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents(String query, Long departmentId, Integer semester) {
        List<Student> students;

        if (query != null && !query.trim().isEmpty()) {
            students = studentRepository.searchStudents(query.trim());
        } else if (departmentId != null) {
            students = studentRepository.findByDepartmentId(departmentId);
        } else if (semester != null) {
            students = studentRepository.findBySemester(semester);
        } else {
            students = studentRepository.findAll();
        }

        // Apply secondary filters in-memory if multiple are provided
        return students.stream()
                .filter(s -> departmentId == null || (s.getDepartment() != null && s.getDepartment().getId().equals(departmentId)))
                .filter(s -> semester == null || s.getSemester() == semester)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.departmentId()));

        student.updateContactInformation(request.name(), request.phone());
        student.setSemester(request.semester());
        student.assignDepartment(department);

        Student updated = studentRepository.save(student);
        auditLogService.recordLog(
                AuditAction.UPDATE_STUDENT,
                "Student",
                updated.getId().toString(),
                "Updated student ID: " + updated.getId() + ", roll: " + updated.getRollNumber()
        );
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public StudentResponse promoteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        // Calls encapsulated domain method enforcing max semester rule
        student.promoteToNextSemester();

        Student updated = studentRepository.save(student);
        auditLogService.recordLog(
                AuditAction.PROMOTE_STUDENT,
                "Student",
                updated.getId().toString(),
                "Promoted student " + updated.getRollNumber() + " to semester " + updated.getSemester()
        );
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        if (!student.getEnrollments().isEmpty()) {
            throw new IllegalStateException("Cannot delete student with historical course enrollment records.");
        }

        userAccountRepository.findByPersonId(id).ifPresent(userAccountRepository::delete);
        studentRepository.delete(student);
    }

    private StudentResponse mapToResponse(Student s) {
        int enrollmentCount = s.getEnrollments() != null ? s.getEnrollments().size() : 0;
        return new StudentResponse(
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getPhone(),
                s.getRollNumber(),
                s.getSemester(),
                s.getDepartment() != null ? s.getDepartment().getId() : null,
                s.getDepartment() != null ? s.getDepartment().getName() : null,
                enrollmentCount,
                s.getAccountStatus(),
                s.getCreatedAt()
        );
    }
}
