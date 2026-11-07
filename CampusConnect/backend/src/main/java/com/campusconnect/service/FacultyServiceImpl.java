package com.campusconnect.service;

import com.campusconnect.dto.CreateFacultyRequest;
import com.campusconnect.dto.FacultyResponse;
import com.campusconnect.dto.UpdateFacultyRequest;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.Faculty;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.Role;
import com.campusconnect.repository.CourseRepository;
import com.campusconnect.repository.DepartmentRepository;
import com.campusconnect.repository.FacultyRepository;
import com.campusconnect.repository.PersonRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyServiceImpl(
            FacultyRepository facultyRepository,
            PersonRepository personRepository,
            DepartmentRepository departmentRepository,
            UserAccountRepository userAccountRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.facultyRepository = facultyRepository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public FacultyResponse createFaculty(CreateFacultyRequest request) {
        if (personRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new IllegalArgumentException("User with email '" + request.email() + "' already exists.");
        }
        if (facultyRepository.existsByEmployeeCode(request.employeeCode().trim().toUpperCase())) {
            throw new IllegalArgumentException("Faculty with employee code '" + request.employeeCode() + "' already exists.");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.departmentId()));

        Faculty faculty = new Faculty(
                request.name(),
                request.email(),
                request.phone(),
                request.employeeCode(),
                request.designation(),
                department
        );

        Faculty savedFaculty = facultyRepository.save(faculty);

        // Derive username from email prefix or employeeCode
        String username = request.email().split("@")[0].toLowerCase();
        if (userAccountRepository.existsByUsername(username)) {
            username = request.employeeCode().toLowerCase().replace("-", "");
        }

        UserAccount account = new UserAccount(
                savedFaculty,
                username,
                passwordEncoder.encode(request.initialPassword()),
                Role.ROLE_FACULTY
        );
        userAccountRepository.save(account);

        return mapToResponse(savedFaculty);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponse getFacultyById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));
        return mapToResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getAllFaculty() {
        return facultyRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponse> getFacultyByDepartment(Long departmentId) {
        return facultyRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.departmentId()));

        faculty.updateContactInformation(request.name(), request.phone());
        faculty.updateDesignation(request.designation());
        faculty.assignDepartment(department);

        Faculty updated = facultyRepository.save(faculty);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));

        var courses = courseRepository.findByInstructorId(id);
        if (!courses.isEmpty()) {
            throw new IllegalStateException("Cannot delete faculty member currently assigned to courses. Unassign courses first.");
        }

        userAccountRepository.findByPersonId(id).ifPresent(userAccountRepository::delete);
        facultyRepository.delete(faculty);
    }

    private FacultyResponse mapToResponse(Faculty f) {
        int coursesCount = f.getAssignedCourses() != null ? f.getAssignedCourses().size() : 0;
        return new FacultyResponse(
                f.getId(),
                f.getName(),
                f.getEmail(),
                f.getPhone(),
                f.getEmployeeCode(),
                f.getDesignation(),
                f.getDepartment() != null ? f.getDepartment().getId() : null,
                f.getDepartment() != null ? f.getDepartment().getName() : null,
                coursesCount,
                f.getAccountStatus(),
                f.getCreatedAt()
        );
    }
}
