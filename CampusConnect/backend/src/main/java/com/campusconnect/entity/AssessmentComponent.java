package com.campusconnect.entity;

import com.campusconnect.enums.AssessmentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * AssessmentComponent - Represents an evaluative piece of coursework (e.g. Midterm, Lab, Final Exam).
 * Demonstrates:
 * 1. Composition - Strong lifecycle dependency on Course.
 * 2. Encapsulation - Enforces positive marks and valid weightage percentages.
 */
@Entity
@Table(name = "course_assessments")
public class AssessmentComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssessmentType type;

    @Column(name = "max_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxMarks;

    @Column(name = "weightage_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightagePercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AssessmentComponent() {
    }

    public AssessmentComponent(Course course, String name, AssessmentType type, BigDecimal maxMarks, BigDecimal weightagePercentage) {
        this.course = course;
        this.name = Objects.requireNonNull(name, "Assessment name must not be null").trim();
        this.type = Objects.requireNonNull(type, "Assessment type must not be null");
        setMaxMarks(maxMarks);
        setWeightagePercentage(weightagePercentage);
        this.createdAt = Instant.now();
    }

    // Business Methods enforcing Encapsulation
    public void setMaxMarks(BigDecimal maxMarks) {
        if (maxMarks == null || maxMarks.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Max marks must be strictly positive. Provided: " + maxMarks);
        }
        this.maxMarks = maxMarks;
    }

    public void setWeightagePercentage(BigDecimal weightagePercentage) {
        if (weightagePercentage == null || weightagePercentage.compareTo(BigDecimal.ZERO) <= 0 || weightagePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Weightage must be between 0 and 100%. Provided: " + weightagePercentage);
        }
        this.weightagePercentage = weightagePercentage;
    }

    void setCourse(Course course) {
        this.course = course;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public AssessmentType getType() {
        return type;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public BigDecimal getWeightagePercentage() {
        return weightagePercentage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentComponent that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
