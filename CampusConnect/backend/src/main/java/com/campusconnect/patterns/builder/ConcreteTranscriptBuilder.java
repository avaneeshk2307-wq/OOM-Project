package com.campusconnect.patterns.builder;

import com.campusconnect.entity.Student;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Concrete Builder implementing TranscriptBuilder.
 * Maintains intermediate product assembly state and performs statistical and standing calculations.
 * Demonstrates GoF Builder Pattern, Encapsulation, and Single Responsibility Principle.
 */
public class ConcreteTranscriptBuilder implements TranscriptBuilder {

    private AcademicTranscriptReport.TranscriptHeader header;
    private AcademicTranscriptReport.StudentBio studentBio;
    private final Map<Integer, List<AcademicTranscriptReport.CourseGradeEntry>> semesterCoursesMap = new TreeMap<>();
    private AcademicTranscriptReport.TranscriptSummary summary;
    private AcademicTranscriptReport.TranscriptSignatures signatures;

    @Override
    public TranscriptBuilder buildHeader(String universityName, String documentTitle) {
        String transcriptNumber = "TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String verificationHash = "SHA256-" + Integer.toHexString(Objects.hash(transcriptNumber, Instant.now()));

        this.header = new AcademicTranscriptReport.TranscriptHeader(
                universityName != null ? universityName : "CampusConnect University",
                documentTitle != null ? documentTitle : "Official Academic Transcript of Records",
                transcriptNumber,
                Instant.now(),
                verificationHash
        );
        return this;
    }

    @Override
    public TranscriptBuilder buildStudentBio(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        this.studentBio = new AcademicTranscriptReport.StudentBio(
                student.getId(),
                student.getName(),
                student.getRollNumber(),
                student.getDepartment() != null ? student.getDepartment().getCode() : "GEN",
                student.getDepartment() != null ? student.getDepartment().getName() : "General Studies",
                student.getSemester(),
                student.getEmail()
        );
        return this;
    }

    @Override
    public TranscriptBuilder addCourseGrade(int semester, String courseCode, String courseTitle,
                                            int credits, BigDecimal marksObtained, String letterGrade,
                                            double gradePoint, String remark) {
        semesterCoursesMap.computeIfAbsent(semester, k -> new ArrayList<>()).add(
                new AcademicTranscriptReport.CourseGradeEntry(
                        courseCode,
                        courseTitle,
                        credits,
                        marksObtained,
                        letterGrade,
                        gradePoint,
                        remark
                )
        );
        return this;
    }

    @Override
    public TranscriptBuilder calculateAcademicSummary() {
        int totalCreditsAttempted = 0;
        int totalCreditsEarned = 0;
        double weightedGradePointsSum = 0.0;
        int totalCourses = 0;

        for (List<AcademicTranscriptReport.CourseGradeEntry> courses : semesterCoursesMap.values()) {
            for (AcademicTranscriptReport.CourseGradeEntry c : courses) {
                totalCreditsAttempted += c.credits();
                totalCourses++;
                if (c.gradePoint() > 0.0) {
                    totalCreditsEarned += c.credits();
                }
                weightedGradePointsSum += (c.gradePoint() * c.credits());
            }
        }

        double cgpa = totalCreditsAttempted > 0
                ? Math.round((weightedGradePointsSum / totalCreditsAttempted) * 100.0) / 100.0
                : 0.0;

        String standing;
        if (cgpa >= 3.8) {
            standing = "First Class with Distinction (Dean's List)";
        } else if (cgpa >= 3.0) {
            standing = "First Class Honours (Good Standing)";
        } else if (cgpa >= 2.0) {
            standing = "Second Class (Satisfactory Standing)";
        } else if (totalCreditsAttempted == 0) {
            standing = "Enrolled (In Progress)";
        } else {
            standing = "Academic Warning (Probation)";
        }

        this.summary = new AcademicTranscriptReport.TranscriptSummary(
                totalCreditsAttempted,
                totalCreditsEarned,
                cgpa,
                standing,
                totalCourses
        );
        return this;
    }

    @Override
    public TranscriptBuilder buildSignatures(String registrarName, String officialSeal) {
        this.signatures = new AcademicTranscriptReport.TranscriptSignatures(
                registrarName != null ? registrarName : "Office of Academic Registrar",
                officialSeal != null ? officialSeal : "OFFICIAL_UNIVERSITY_ACCREDITED_SEAL",
                "https://campusconnect.edu/verify/transcript/" + (header != null ? header.transcriptNumber() : "DIRECT")
        );
        return this;
    }

    @Override
    public AcademicTranscriptReport build() {
        if (this.header == null) {
            buildHeader(null, null);
        }
        if (this.summary == null) {
            calculateAcademicSummary();
        }
        if (this.signatures == null) {
            buildSignatures(null, null);
        }

        List<AcademicTranscriptReport.SemesterTranscript> semesterList = new ArrayList<>();
        for (Map.Entry<Integer, List<AcademicTranscriptReport.CourseGradeEntry>> entry : semesterCoursesMap.entrySet()) {
            int sem = entry.getKey();
            List<AcademicTranscriptReport.CourseGradeEntry> courses = entry.getValue();

            int semCredits = courses.stream().mapToInt(AcademicTranscriptReport.CourseGradeEntry::credits).sum();
            double semPoints = courses.stream().mapToDouble(c -> c.gradePoint() * c.credits()).sum();
            double semGpa = semCredits > 0 ? Math.round((semPoints / semCredits) * 100.0) / 100.0 : 0.0;

            semesterList.add(new AcademicTranscriptReport.SemesterTranscript(sem, courses, semCredits, semGpa));
        }

        return new AcademicTranscriptReport(
                this.header,
                this.studentBio,
                semesterList,
                this.summary,
                this.signatures
        );
    }
}
