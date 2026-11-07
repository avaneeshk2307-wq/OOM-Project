package com.campusconnect.patterns.builder;

import com.campusconnect.entity.Course;
import com.campusconnect.entity.Enrollment;
import com.campusconnect.entity.Grade;
import com.campusconnect.entity.Student;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.patterns.strategy.GradingResult;
import com.campusconnect.patterns.strategy.GradingStrategy;
import com.campusconnect.repository.GradeRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Director in the GoF Builder Pattern.
 * Enforces the strict sequential workflow for assembling an AcademicTranscriptReport:
 * 1. Header -> 2. Student Bio -> 3. Semester Courses & Evaluated Grades -> 4. Summary & CGPA -> 5. Verification Signatures
 */
@Component
public class TranscriptDirector {

    public AcademicTranscriptReport constructTranscript(
            TranscriptBuilder builder,
            Student student,
            List<Enrollment> enrollments,
            GradeRepository gradeRepository,
            GradingStrategy gradingStrategy
    ) {
        // Step 1: Initialize official transcript header
        builder.buildHeader("CampusConnect University of Technology", "Official Student Academic Transcript");

        // Step 2: Assemble student identity and departmental affiliation
        builder.buildStudentBio(student);

        // Step 3: Extract semester enrollments and map evaluated course grades
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == EnrollmentStatus.ENROLLED || enrollment.getStatus() == EnrollmentStatus.APPROVED) {
                Course course = enrollment.getCourse();
                List<Grade> grades = gradeRepository.findByEnrollmentId(enrollment.getId());

                BigDecimal totalWeightedScore = BigDecimal.ZERO;
                for (Grade g : grades) {
                    BigDecimal componentScore = g.getMarksObtained()
                            .multiply(g.getAssessment().getWeightagePercentage())
                            .divide(g.getAssessment().getMaxMarks(), 2, RoundingMode.HALF_UP);
                    totalWeightedScore = totalWeightedScore.add(componentScore);
                }

                GradingResult overallResult = gradingStrategy.calculateGrade(totalWeightedScore, BigDecimal.valueOf(100));

                builder.addCourseGrade(
                        enrollment.getSemester(),
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        totalWeightedScore,
                        overallResult.gradeLetter().getDisplay(),
                        overallResult.gradePoint(),
                        overallResult.remark()
                );
            }
        }

        // Step 4: Calculate GPA, credits, and academic standing
        builder.calculateAcademicSummary();

        // Step 5: Attach official institutional seals
        builder.buildSignatures("Dr. Elizabeth Vance, Registrar", "CAMPUSCONNECT_SEAL_AUTHENTICATED");

        // Step 6: Construct and return the immutable product
        return builder.build();
    }
}
