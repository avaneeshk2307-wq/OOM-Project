package com.campusconnect.patterns.builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Product of the GoF Builder Pattern.
 * Represents a complete, multi-part official university academic transcript.
 */
public record AcademicTranscriptReport(
        TranscriptHeader header,
        StudentBio studentBio,
        List<SemesterTranscript> semesterRecords,
        TranscriptSummary summary,
        TranscriptSignatures signatures
) {
    public record TranscriptHeader(
            String universityName,
            String documentTitle,
            String transcriptNumber,
            Instant issueDate,
            String verificationHash
    ) {}

    public record StudentBio(
            Long studentId,
            String fullName,
            String rollNumber,
            String departmentCode,
            String departmentName,
            int currentSemester,
            String email
    ) {}

    public record CourseGradeEntry(
            String courseCode,
            String courseTitle,
            int credits,
            BigDecimal marksObtained,
            String letterGrade,
            double gradePoint,
            String remark
    ) {}

    public record SemesterTranscript(
            int semester,
            List<CourseGradeEntry> courses,
            int semesterCredits,
            double semesterGpa
    ) {}

    public record TranscriptSummary(
            int totalCreditsAttempted,
            int totalCreditsEarned,
            double cumulativeGpa,
            String academicStanding,
            int totalCoursesCompleted
    ) {}

    public record TranscriptSignatures(
            String registrarName,
            String officialSeal,
            String verificationUrl
    ) {}
}

