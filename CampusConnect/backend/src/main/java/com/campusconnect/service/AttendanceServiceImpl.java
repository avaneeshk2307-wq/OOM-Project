package com.campusconnect.service;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.enums.AttendanceStatus;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceServiceImpl(
            AttendanceSessionRepository attendanceSessionRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public AttendanceSessionResponse createSession(CreateSessionRequest request, String username) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        AttendanceSession session = new AttendanceSession(course, request.sessionDate(), request.topic());

        if (request.records() != null && !request.records().isEmpty()) {
            for (RecordEntryRequest rec : request.records()) {
                Student student = studentRepository.findById(rec.studentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + rec.studentId()));
                session.recordStudentAttendance(student, rec.status(), rec.remarks());
            }
        }

        AttendanceSession saved = attendanceSessionRepository.save(session);
        return mapToSessionResponse(saved);
    }

    @Override
    public AttendanceSessionResponse markAttendance(Long sessionId, List<RecordEntryRequest> records, String username) {
        AttendanceSession session = attendanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found with id: " + sessionId));

        for (RecordEntryRequest rec : records) {
            Student student = studentRepository.findById(rec.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + rec.studentId()));
            session.recordStudentAttendance(student, rec.status(), rec.remarks());
        }

        AttendanceSession saved = attendanceSessionRepository.save(session);
        return mapToSessionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSessionResponse> getSessionsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }

        List<AttendanceSession> sessions = attendanceSessionRepository.findByCourseIdOrderBySessionDateDesc(courseId);
        return sessions.stream().map(this::mapToSessionResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSessionResponse getSessionById(Long sessionId) {
        AttendanceSession session = attendanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found with id: " + sessionId));
        return mapToSessionResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseAttendanceSummary> getStudentAttendanceSummary(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<StudentCourseAttendanceSummary> summaries = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == EnrollmentStatus.ENROLLED || enrollment.getStatus() == EnrollmentStatus.APPROVED) {
                Course course = enrollment.getCourse();
                long totalSessions = attendanceRecordRepository.countTotalByCourseAndStudent(course.getId(), studentId);
                long attendedSessions = attendanceRecordRepository.countAttendedByCourseAndStudent(course.getId(), studentId);

                double percentage = totalSessions > 0
                        ? Math.round(((double) attendedSessions / totalSessions) * 1000.0) / 10.0
                        : 100.0;
                boolean warning = totalSessions > 0 && percentage < 75.0;

                List<AttendanceRecord> records = attendanceRecordRepository.findByCourseIdAndStudentId(course.getId(), studentId);
                List<StudentSessionHistoryItem> history = records.stream()
                        .sorted(Comparator.comparing((AttendanceRecord r) -> r.getSession().getSessionDate()).reversed())
                        .map(r -> new StudentSessionHistoryItem(
                                r.getSession().getId(),
                                r.getSession().getSessionDate(),
                                r.getSession().getTopic(),
                                r.getStatus(),
                                r.getRemarks()
                        ))
                        .toList();

                summaries.add(new StudentCourseAttendanceSummary(
                        course.getId(),
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        totalSessions,
                        attendedSessions,
                        percentage,
                        warning,
                        history
                ));
            }
        }

        return summaries;
    }

    private AttendanceSessionResponse mapToSessionResponse(AttendanceSession session) {
        List<AttendanceRecordResponse> recordResponses = session.getRecords().stream()
                .map(r -> new AttendanceRecordResponse(
                        r.getId(),
                        r.getStudent().getId(),
                        r.getStudent().getName(),
                        r.getStudent().getRollNumber(),
                        r.getStatus(),
                        r.getRemarks()
                ))
                .toList();

        return new AttendanceSessionResponse(
                session.getId(),
                session.getCourse().getId(),
                session.getCourse().getCode(),
                session.getCourse().getTitle(),
                session.getSessionDate(),
                session.getTopic(),
                session.getRecords().size(),
                session.getPresentCount(),
                session.getCreatedAt(),
                recordResponses
        );
    }
}
