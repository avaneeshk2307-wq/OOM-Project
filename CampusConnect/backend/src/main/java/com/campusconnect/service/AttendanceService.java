package com.campusconnect.service;

import com.campusconnect.dto.AttendanceSessionResponse;
import com.campusconnect.dto.CreateSessionRequest;
import com.campusconnect.dto.RecordEntryRequest;
import com.campusconnect.dto.StudentCourseAttendanceSummary;

import java.util.List;

public interface AttendanceService {
    AttendanceSessionResponse createSession(CreateSessionRequest request, String username);
    AttendanceSessionResponse markAttendance(Long sessionId, List<RecordEntryRequest> records, String username);
    List<AttendanceSessionResponse> getSessionsByCourse(Long courseId);
    AttendanceSessionResponse getSessionById(Long sessionId);
    List<StudentCourseAttendanceSummary> getStudentAttendanceSummary(Long studentId);
}
