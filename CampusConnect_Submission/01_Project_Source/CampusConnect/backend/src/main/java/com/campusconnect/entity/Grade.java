package com.campusconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Grade - Records marks and letter grade for a student's enrollment in a course assessment.
 * Demonstrates:
 * 1. Encapsulation - Enforces non-negative marks and compares against assessment maximums.
 * 2. Association - Bridges Enrollment and AssessmentComponent.
 */
@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(name = "uq_enrollment_assessment", columnNames = {"enrollment_id", "assessment_id"})
})
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private AssessmentComponent assessment;

    @Column(name = "marks_obtained", nullable = false, precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "letter_grade", length = 10)
    private String letterGrade;

    @Column(name = "graded_at", nullable = false, updatable = false)
    private Instant gradedAt = Instant.now();

    protected Grade() {
    }

    public Grade(Enrollment enrollment, AssessmentComponent assessment, BigDecimal marksObtained, String letterGrade) {
        this.enrollment = Objects.requireNonNull(enrollment, "Enrollment must not be null");
        this.assessment = Objects.requireNonNull(assessment, "Assessment must not be null");
        setMarks(marksObtained, letterGrade);
        this.gradedAt = Instant.now();
    }

    // Business Methods enforcing Encapsulation
    public void setMarks(BigDecimal marks, String letterGrade) {
        Objects.requireNonNull(marks, "Marks obtained must not be null");
        if (marks.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Marks obtained cannot be negative. Provided: " + marks);
        }
        if (this.assessment != null && marks.compareTo(this.assessment.getMaxMarks()) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Marks obtained (%s) cannot exceed assessment maximum (%s).",
                    marks, this.assessment.getMaxMarks()
            ));
        }
        this.marksObtained = marks;
        this.letterGrade = letterGrade;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public AssessmentComponent getAssessment() {
        return assessment;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade grade)) return false;
        return id != null && Objects.equals(id, grade.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
