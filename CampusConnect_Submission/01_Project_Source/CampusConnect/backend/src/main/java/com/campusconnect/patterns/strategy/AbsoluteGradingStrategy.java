package com.campusconnect.patterns.strategy;

import com.campusconnect.enums.GradeLetter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * AbsoluteGradingStrategy - Concrete Strategy implementing standard criterion-referenced grading.
 * Maps percentage scores to academic letter grades according to fixed grade brackets.
 */
@Component("absoluteGradingStrategy")
public class AbsoluteGradingStrategy implements GradingStrategy {

    @Override
    public GradingResult calculateGrade(BigDecimal marksObtained, BigDecimal maxMarks) {
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
        GradeLetter letter;

        if (percentage >= 90.0) {
            letter = GradeLetter.A_PLUS;
        } else if (percentage >= 80.0) {
            letter = GradeLetter.A;
        } else if (percentage >= 75.0) {
            letter = GradeLetter.B_PLUS;
        } else if (percentage >= 70.0) {
            letter = GradeLetter.B;
        } else if (percentage >= 65.0) {
            letter = GradeLetter.C_PLUS;
        } else if (percentage >= 60.0) {
            letter = GradeLetter.C;
        } else if (percentage >= 50.0) {
            letter = GradeLetter.D;
        } else {
            letter = GradeLetter.F;
        }

        return new GradingResult(letter, letter.getGradePoint(), percentage, letter.getDescription());
    }

    @Override
    public String getStrategyName() {
        return "Absolute Criterion-Referenced Grading";
    }
}
