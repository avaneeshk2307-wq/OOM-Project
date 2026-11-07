package com.campusconnect.service;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.exception.TimetableConflictException;
import com.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public TimetableService(TimetableRepository timetableRepository,
                            ClassroomRepository classroomRepository,
                            CourseRepository courseRepository,
                            FacultyRepository facultyRepository,
                            StudentRepository studentRepository,
                            EnrollmentRepository enrollmentRepository) {
        this.timetableRepository = timetableRepository;
        this.classroomRepository = classroomRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // --- Classroom Management ---

    public ClassroomResponse createClassroom(CreateClassroomRequest request) {
        if (classroomRepository.existsByRoomNumber(request.roomNumber().trim().toUpperCase())) {
            throw new IllegalArgumentException("Classroom with room number '" + request.roomNumber() + "' already exists");
        }

        Classroom classroom = new Classroom(
                request.roomNumber(),
                request.building(),
                request.capacity(),
                request.roomType()
        );
        Classroom saved = classroomRepository.save(classroom);
        return mapToClassroomResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClassroomResponse> getAllClassrooms() {
        return classroomRepository.findAll().stream()
                .map(this::mapToClassroomResponse)
                .toList();
    }

    public void deleteClassroom(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
        List<TimetableEntry> entries = timetableRepository.findByClassroomId(id);
        if (!entries.isEmpty()) {
            throw new IllegalStateException("Cannot delete classroom that has " + entries.size() + " active timetable entries scheduled");
        }
        classroomRepository.delete(classroom);
    }

    // --- Timetable Scheduling & Conflict Detection ---

    public TimetableResponse createTimetableEntry(CreateTimetableRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        Classroom classroom = classroomRepository.findById(request.classroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + request.classroomId()));

        Faculty faculty;
        if (request.facultyId() != null) {
            faculty = facultyRepository.findById(request.facultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + request.facultyId()));
        } else if (course.getInstructor() != null) {
            faculty = course.getInstructor();
        } else {
            throw new IllegalArgumentException("Course has no assigned instructor. Please specify a faculty ID.");
        }

        // Room capacity check
        if (!classroom.canAccommodate(course.getCapacity())) {
            throw new IllegalArgumentException(String.format(
                    "Classroom %s capacity (%d) cannot accommodate course %s capacity (%d)",
                    classroom.getRoomNumber(), classroom.getCapacity(), course.getCode(), course.getCapacity()));
        }

        // 1. Classroom Conflict Check
        List<TimetableEntry> roomConflicts = timetableRepository.findClassroomConflicts(
                classroom.getId(), request.dayOfWeek(), request.startTime(), request.endTime());
        if (!roomConflicts.isEmpty()) {
            TimetableEntry conflict = roomConflicts.get(0);
            throw new TimetableConflictException(String.format(
                    "Classroom %s is already occupied on %s between %s and %s by course %s",
                    classroom.getRoomNumber(), request.dayOfWeek(),
                    conflict.getStartTime(), conflict.getEndTime(),
                    conflict.getCourse().getCode()));
        }

        // 2. Faculty Conflict Check
        List<TimetableEntry> facultyConflicts = timetableRepository.findFacultyConflicts(
                faculty.getId(), request.dayOfWeek(), request.startTime(), request.endTime());
        if (!facultyConflicts.isEmpty()) {
            TimetableEntry conflict = facultyConflicts.get(0);
            throw new TimetableConflictException(String.format(
                    "Faculty %s is already scheduled to teach course %s on %s between %s and %s in room %s",
                    faculty.getName(), conflict.getCourse().getCode(), request.dayOfWeek(),
                    conflict.getStartTime(), conflict.getEndTime(),
                    conflict.getClassroom().getRoomNumber()));
        }

        // 3. Course Conflict Check
        List<TimetableEntry> courseConflicts = timetableRepository.findCourseConflicts(
                course.getId(), request.dayOfWeek(), request.startTime(), request.endTime());
        if (!courseConflicts.isEmpty()) {
            TimetableEntry conflict = courseConflicts.get(0);
            throw new TimetableConflictException(String.format(
                    "Course %s already has another lecture scheduled on %s between %s and %s",
                    course.getCode(), request.dayOfWeek(), conflict.getStartTime(), conflict.getEndTime()));
        }

        TimetableEntry entry = new TimetableEntry(
                course, classroom, faculty,
                request.dayOfWeek(), request.startTime(), request.endTime()
        );

        TimetableEntry saved = timetableRepository.save(entry);
        return mapToTimetableResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getAllTimetables() {
        return timetableRepository.findAll().stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByFaculty(Long facultyId) {
        return timetableRepository.findByFacultyId(facultyId).stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByCourse(Long courseId) {
        return timetableRepository.findByCourseId(courseId).stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesForStudent(Long studentId) {
        // Find enrolled/approved courses for student
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ENROLLED);
        if (enrollments.isEmpty()) {
            // Also include APPROVED status if enrolled is empty
            enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.APPROVED);
        }

        List<Long> courseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .toList();

        if (courseIds.isEmpty()) {
            return List.of();
        }

        return timetableRepository.findByCourseIdIn(courseIds).stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    public void deleteTimetableEntry(Long id) {
        TimetableEntry entry = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found with id: " + id));
        timetableRepository.delete(entry);
    }

    private ClassroomResponse mapToClassroomResponse(Classroom c) {
        return new ClassroomResponse(
                c.getId(),
                c.getRoomNumber(),
                c.getBuilding(),
                c.getCapacity(),
                c.getRoomType(),
                c.getCreatedAt()
        );
    }

    private TimetableResponse mapToTimetableResponse(TimetableEntry t) {
        return new TimetableResponse(
                t.getId(),
                t.getCourse().getId(),
                t.getCourse().getCode(),
                t.getCourse().getTitle(),
                t.getClassroom().getId(),
                t.getClassroom().getRoomNumber(),
                t.getClassroom().getBuilding(),
                t.getClassroom().getCapacity(),
                t.getFaculty().getId(),
                t.getFaculty().getName(),
                t.getDayOfWeek(),
                t.getStartTime(),
                t.getEndTime(),
                t.getCreatedAt()
        );
    }
}
