package com.campusconnect.service;

import com.campusconnect.dto.*;

import java.util.List;

public interface GradeService {
    AssessmentResponse createAssessment(CreateAssessmentRequest request, String username);
    List<AssessmentResponse> getAssessmentsByCourse(Long courseId);
    GradeResponse recordGrade(SubmitGradeRequest request, String username);
    List<GradeResponse> recordBatchGrades(List<SubmitGradeRequest> requests, String username);
    List<GradeResponse> getGradesByCourse(Long courseId);
    List<GradeResponse> getGradesByStudent(Long studentId);
    List<StudentCourseGradeReport> getStudentGradeReports(Long studentId);
    CourseGradeDistributionResponse getCourseGradeDistribution(Long courseId);
}
