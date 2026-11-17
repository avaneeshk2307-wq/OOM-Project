package com.campusconnect.patterns.strategy;

import com.campusconnect.enums.GradeLetter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * RelativeGradingStrategy - Concrete Strategy implementing norm-referenced grading.
 * Assigns grades based on statistical distribution (Gaussian curve / Z-score) relative to the cohort mean.
 * Demonstrates GoF Strategy Pattern, Abstraction, and Polymorphism.
 */
@Component("relativeGradingStrategy")
public class RelativeGradingStrategy implements GradingStrategy {

    public static final double DEFAULT_COHORT_MEAN = 70.0;
    public static final double DEFAULT_COHORT_STD_DEV = 12.0;

    @Override
    public GradingResult calculateGrade(BigDecimal marksObtained, BigDecimal maxMarks) {
        return calculateGradeWithCohort(marksObtained, maxMarks, DEFAULT_COHORT_MEAN, DEFAULT_COHORT_STD_DEV);
    }

    /**
     * Calculates curved grade using dynamic cohort mean and standard deviation.
     * Z-score = (percentage - mean) / stdDev
     */
    public GradingResult calculateGradeWithCohort(BigDecimal marksObtained, BigDecimal maxMarks, double mean, double stdDev) {
        Objects.requireNonNull(marksObtained, "Marks obtained must not be null");
        Objects.requireNonNull(maxMarks, "Max marks must not be null");

        if (maxMarks.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Max marks must be greater than zero. Provided: " + maxMarks);
        }

        if (marksObtained.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Marks obtained cannot be negative. Provided: " + marksObtained);
        }

        BigDecimal percentageBd = marksObtained
                .multiply(BigDecimal.valueOf(100))
                .divide(maxMarks, 2, RoundingMode.HALF_UP);

        double percentage = percentageBd.doubleValue();
        double effectiveStdDev = stdDev > 0 ? stdDev : DEFAULT_COHORT_STD_DEV;
        double zScore = (percentage - mean) / effectiveStdDev;

        GradeLetter letter;
        String remark;

        if (zScore >= 1.5) {
            letter = GradeLetter.A_PLUS;
            remark = "Outstanding (Top 7% Normative Cohort)";
        } else if (zScore >= 1.0) {
            letter = GradeLetter.A;
            remark = "Excellent (Upper 15% of Class)";
        } else if (zScore >= 0.5) {
            letter = GradeLetter.B_PLUS;
            remark = "Very Good (Above Average)";
        } else if (zScore >= 0.0) {
            letter = GradeLetter.B;
            remark = "Good (Class Average)";
        } else if (zScore >= -0.5) {
            letter = GradeLetter.C_PLUS;
            remark = "Fair (Slightly Below Average)";
        } else if (zScore >= -1.0) {
            letter = GradeLetter.C;
            remark = "Satisfactory (Lower Quadrant)";
        } else if (zScore >= -1.5) {
            letter = GradeLetter.D;
            remark = "Marginal Pass";
        } else {
            letter = GradeLetter.F;
            remark = "Fail (Significant Deficit Below Norm)";
        }

        return new GradingResult(letter, letter.getGradePoint(), percentage, remark);
    }

    @Override
    public String getStrategyName() {
        return "Relative Norm-Referenced Curve Grading (Gaussian Distribution)";
    }
}

