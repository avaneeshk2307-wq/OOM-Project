package com.campusconnect.patterns.builder;

import com.campusconnect.entity.Department;
import com.campusconnect.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptBuilderTest {

    @Test
    @DisplayName("Builder correctly constructs complex AcademicTranscriptReport with semester courses and Dean's List standing")
    void shouldBuildDeanListTranscript() {
        Department cs = new Department("CS", "Computer Science", "CS Dept");
        Student student = new Student("Alice Turing", "alice@campus.edu", "555-0101", "CS2026-007", 1, cs);

        TranscriptBuilder builder = new ConcreteTranscriptBuilder();

        AcademicTranscriptReport report = builder
                .buildHeader("CampusConnect University of Technology", "Official Transcript")
                .buildStudentBio(student)
                .addCourseGrade(1, "CS101", "OOP with Java", 4, BigDecimal.valueOf(95), "A+", 4.0, "Outstanding")
                .addCourseGrade(1, "CS102", "Data Structures", 4, BigDecimal.valueOf(90), "A+", 4.0, "Outstanding")
                .calculateAcademicSummary()
                .buildSignatures("Dr. Vance, Registrar", "OFFICIAL_SEAL")
                .build();

        assertThat(report).isNotNull();
        assertThat(report.header().documentTitle()).isEqualTo("Official Transcript");
        assertThat(report.studentBio().fullName()).isEqualTo("Alice Turing");
        assertThat(report.studentBio().rollNumber()).isEqualTo("CS2026-007");

        // Semester Verification
        assertThat(report.semesterRecords()).hasSize(1);
        AcademicTranscriptReport.SemesterTranscript sem1 = report.semesterRecords().get(0);
        assertThat(sem1.semester()).isEqualTo(1);
        assertThat(sem1.semesterCredits()).isEqualTo(8);
        assertThat(sem1.semesterGpa()).isEqualTo(4.0);

        // Summary Verification
        assertThat(report.summary().totalCreditsAttempted()).isEqualTo(8);
        assertThat(report.summary().totalCreditsEarned()).isEqualTo(8);
        assertThat(report.summary().cumulativeGpa()).isEqualTo(4.0);
        assertThat(report.summary().academicStanding()).contains("Dean's List");

        // Signatures Verification
        assertThat(report.signatures().officialSeal()).isEqualTo("OFFICIAL_SEAL");
    }

    @Test
    @DisplayName("Builder detects academic probation when CGPA is below 2.0")
    void shouldDetectAcademicProbation() {
        Department cs = new Department("CS", "Computer Science", "CS Dept");
        Student student = new Student("Bob Smith", "bob@campus.edu", "555-0102", "CS2026-008", 1, cs);

        TranscriptBuilder builder = new ConcreteTranscriptBuilder();

        AcademicTranscriptReport report = builder
                .buildHeader(null, null)
                .buildStudentBio(student)
                .addCourseGrade(1, "CS101", "OOP with Java", 4, BigDecimal.valueOf(45), "D", 1.0, "Marginal Pass")
                .addCourseGrade(1, "CS102", "Calculus", 4, BigDecimal.valueOf(30), "F", 0.0, "Fail")
                .calculateAcademicSummary()
                .build();

        assertThat(report.summary().totalCreditsAttempted()).isEqualTo(8);
        assertThat(report.summary().totalCreditsEarned()).isEqualTo(4); // Only D earned credits, F earned 0
        assertThat(report.summary().cumulativeGpa()).isEqualTo(0.5);   // (1*4 + 0*4) / 8 = 0.5
        assertThat(report.summary().academicStanding()).contains("Probation");
    }
}

