package com.campusconnect.patterns.strategy;

import com.campusconnect.enums.GradeLetter;
import com.campusconnect.enums.GradingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RelativeGradingStrategyTest {

    private RelativeGradingStrategy relativeStrategy;
    private AbsoluteGradingStrategy absoluteStrategy;
    private GradingStrategyFactory factory;

    @BeforeEach
    void setUp() {
        relativeStrategy = new RelativeGradingStrategy();
        absoluteStrategy = new AbsoluteGradingStrategy();
        factory = new GradingStrategyFactory(absoluteStrategy, relativeStrategy);
    }

    @Test
    @DisplayName("Curve grading with dynamic hard exam: score of 60% is curved to A+ when cohort mean is 45%")
    void shouldCurveHardExamGracefully() {
        // Exam out of 100, student scored 60. Cohort mean is 45.0, stdDev is 10.0.
        // Z-score = (60 - 45) / 10 = +1.5 -> A+
        GradingResult curvedResult = relativeStrategy.calculateGradeWithCohort(
                BigDecimal.valueOf(60), BigDecimal.valueOf(100), 45.0, 10.0
        );

        assertThat(curvedResult.gradeLetter()).isEqualTo(GradeLetter.A_PLUS);
        assertThat(curvedResult.gradePoint()).isEqualTo(4.0);
        assertThat(curvedResult.remark()).contains("Top 7%");

        // Under Absolute strategy, 60% is only a C
        GradingResult absoluteResult = absoluteStrategy.calculateGrade(
                BigDecimal.valueOf(60), BigDecimal.valueOf(100)
        );
        assertThat(absoluteResult.gradeLetter()).isEqualTo(GradeLetter.C);
        assertThat(absoluteResult.gradePoint()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Evaluate default baseline relative grading brackets")
    void shouldEvaluateDefaultBaselineBrackets() {
        // Mean 70, stdDev 12
        // Score 70 -> Z = 0 -> B (Average)
        GradingResult meanResult = relativeStrategy.calculateGrade(BigDecimal.valueOf(70), BigDecimal.valueOf(100));
        assertThat(meanResult.gradeLetter()).isEqualTo(GradeLetter.B);

        // Score 90 -> Z = (90 - 70) / 12 = 1.67 >= 1.5 -> A+
        GradingResult topResult = relativeStrategy.calculateGrade(BigDecimal.valueOf(90), BigDecimal.valueOf(100));
        assertThat(topResult.gradeLetter()).isEqualTo(GradeLetter.A_PLUS);

        // Score 30 -> Z = (30 - 70) / 12 = -3.33 < -1.5 -> F
        GradingResult failResult = relativeStrategy.calculateGrade(BigDecimal.valueOf(30), BigDecimal.valueOf(100));
        assertThat(failResult.gradeLetter()).isEqualTo(GradeLetter.F);
    }

    @Test
    @DisplayName("Verify GradingStrategyFactory selects correct strategy polymorphically")
    void shouldSelectStrategyPolymorphically() {
        GradingStrategy abs = factory.getStrategy(GradingMode.ABSOLUTE);
        assertThat(abs).isInstanceOf(AbsoluteGradingStrategy.class);
        assertThat(abs.getStrategyName()).contains("Absolute");

        GradingStrategy rel = factory.getStrategy(GradingMode.RELATIVE);
        assertThat(rel).isInstanceOf(RelativeGradingStrategy.class);
        assertThat(rel.getStrategyName()).contains("Relative");

        // String lookup
        assertThat(factory.getStrategyByName("RELATIVE")).isInstanceOf(RelativeGradingStrategy.class);
        assertThat(factory.getStrategyByName("ABSOLUTE")).isInstanceOf(AbsoluteGradingStrategy.class);
        assertThat(factory.getStrategyByName(null)).isInstanceOf(AbsoluteGradingStrategy.class);
    }

    @Test
    @DisplayName("Reject negative marks and zero max marks")
    void shouldValidateInputs() {
        assertThatThrownBy(() -> relativeStrategy.calculateGrade(BigDecimal.valueOf(-5), BigDecimal.valueOf(100)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> relativeStrategy.calculateGrade(BigDecimal.valueOf(50), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
