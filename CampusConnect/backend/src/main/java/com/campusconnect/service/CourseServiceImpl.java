package com.campusconnect.service;

import com.campusconnect.dto.CourseRequest;
import com.campusconnect.dto.CourseResponse;
import com.campusconnect.entity.Course;
import com.campusconnect.entity.Department;
import com.campusconnect.entity.Faculty;
import com.campusconnect.enums.AuditAction;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.repository.CourseRepository;
import com.campusconnect.repository.DepartmentRepository;
import com.campusconnect.repository.EnrollmentRepository;
import com.campusconnect.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuditLogService auditLogService;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            DepartmentRepository departmentRepository,
            FacultyRepository facultyRepository,
            EnrollmentRepository enrollmentRepository,
            AuditLogService auditLogService
    ) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new IllegalArgumentException("Course with code '" + request.code() + "' already exists.");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.departmentId()));

        Faculty instructor = null;
        if (request.instructorId() != null) {
            instructor = facultyRepository.findById(request.instructorId())
                    .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + request.instructorId()));
        }

        Course course = new Course(
                request.code(),
                request.title(),
                request.description(),
                request.credits(),
                request.semester(),
                request.capacity(),
                department,
                instructor
        );

        Course saved = courseRepository.save(course);
        auditLogService.recordLog(
                AuditAction.CREATE_COURSE,
                "Course",
                saved.getId().toString(),
                "Created course: " + saved.getCode() + " - " + saved.getTitle()
        );
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));
        return mapToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByFaculty(Long facultyId) {
        return courseRepository.findByInstructorId(facultyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));

        course.updateDetails(
                request.title(),
                request.description(),
                request.credits(),
                request.semester(),
                request.capacity()
        );

        if (request.instructorId() != null) {
            Faculty faculty = facultyRepository.findById(request.instructorId())
                    .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + request.instructorId()));
            course.assignInstructor(faculty);
        } else {
            course.removeInstructor();
        }

        Course updated = courseRepository.save(course);
        auditLogService.recordLog(
                AuditAction.UPDATE_COURSE,
                "Course",
                updated.getId().toString(),
                "Updated course code/info: " + updated.getCode()
        );
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public CourseResponse assignFaculty(Long courseId, Long facultyId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + facultyId));

        course.assignInstructor(faculty);
        Course updated = courseRepository.save(course);
        auditLogService.recordLog(
                AuditAction.ASSIGN_FACULTY,
                "Course",
                updated.getId().toString(),
                "Assigned faculty " + faculty.getName() + " to course " + updated.getCode()
        );
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));

        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(id, EnrollmentStatus.ENROLLED);
        if (enrolledCount > 0) {
            throw new IllegalStateException("Cannot delete course with active enrollments.");
        }

        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course c) {
        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED);
        return new CourseResponse(
                c.getId(),
                c.getCode(),
                c.getTitle(),
                c.getDescription(),
                c.getCredits(),
                c.getSemester(),
                c.getCapacity(),
                enrolledCount,
                c.getDepartment() != null ? c.getDepartment().getId() : null,
                c.getDepartment() != null ? c.getDepartment().getName() : null,
                c.getInstructor() != null ? c.getInstructor().getId() : null,
                c.getInstructor() != null ? c.getInstructor().getName() : null,
                c.getCreatedAt()
        );
    }
}
