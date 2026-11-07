package com.campusconnect.service;

import com.campusconnect.dto.EnrollmentRegisterRequest;
import com.campusconnect.dto.EnrollmentResponse;
import com.campusconnect.entity.Course;
import com.campusconnect.entity.Enrollment;
import com.campusconnect.entity.Student;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.patterns.state.EnrollmentState;
import com.campusconnect.patterns.state.EnrollmentStateFactory;
import com.campusconnect.repository.CourseRepository;
import com.campusconnect.repository.EnrollmentRepository;
import com.campusconnect.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EnrollmentServiceImpl - Implements course registration and State Pattern lifecycle transitions.
 * Demonstrates:
 * 1. State Design Pattern - Transitions managed polymorphically via EnrollmentState implementations.
 * 2. Business Invariant Enforcement - Duplicate enrollment prevention and course capacity constraints.
 */
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public EnrollmentResponse registerCourse(Long studentId, EnrollmentRegisterRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + request.courseId()));

        int semester = request.semester() != null ? request.semester() : student.getSemester();

        // 1. Business Invariant: Prevent Duplicate Enrollment
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndSemester(studentId, request.courseId(), semester)) {
            throw new IllegalStateException("Student is already registered in course " + course.getCode() + " for semester " + semester);
        }

        // 2. Business Invariant: Capacity Verification
        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(request.courseId(), EnrollmentStatus.ENROLLED);
        if (!course.hasAvailableSeats(enrolledCount)) {
            throw new IllegalStateException("Course " + course.getCode() + " has reached its maximum enrollment capacity (" + course.getCapacity() + ").");
        }

        // 3. Initialize in DRAFT, then submit via State Pattern
        Enrollment enrollment = new Enrollment(student, course, semester);
        EnrollmentState draftState = EnrollmentStateFactory.getState(enrollment.getStatus());
        draftState.submit(enrollment); // Transitions to SUBMITTED

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse approveEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentEntity(enrollmentId);
        EnrollmentState currentState = EnrollmentStateFactory.getState(enrollment.getStatus());

        currentState.approve(enrollment);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EnrollmentResponse finalizeEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentEntity(enrollmentId);

        // Verify capacity once more before final enroll
        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(enrollment.getCourse().getId(), EnrollmentStatus.ENROLLED);
        if (!enrollment.getCourse().hasAvailableSeats(enrolledCount)) {
            throw new IllegalStateException("Course has reached capacity. Cannot finalize enrollment.");
        }

        EnrollmentState currentState = EnrollmentStateFactory.getState(enrollment.getStatus());
        currentState.finalizeEnrollment(enrollment);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EnrollmentResponse dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentEntity(enrollmentId);
        EnrollmentState currentState = EnrollmentStateFactory.getState(enrollment.getStatus());

        currentState.drop(enrollment);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EnrollmentResponse rejectEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentEntity(enrollmentId);
        EnrollmentState currentState = EnrollmentStateFactory.getState(enrollment.getStatus());

        currentState.reject(enrollment);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments(EnrollmentStatus status) {
        List<Enrollment> list = status != null
                ? enrollmentRepository.findAll().stream().filter(e -> e.getStatus() == status).toList()
                : enrollmentRepository.findAll();

        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        return mapToResponse(getEnrollmentEntity(id));
    }

    private Enrollment getEnrollmentEntity(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + id));
    }

    private EnrollmentResponse mapToResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getName(),
                e.getStudent().getRollNumber(),
                e.getCourse().getId(),
                e.getCourse().getCode(),
                e.getCourse().getTitle(),
                e.getCourse().getCredits(),
                e.getSemester(),
                e.getStatus(),
                e.getRegisteredAt()
        );
    }
}
