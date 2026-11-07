package com.campusconnect.patterns.strategy;

import java.math.BigDecimal;

/**
 * GradingStrategy - GoF Strategy Design Pattern interface.
 * Defines the family of algorithms for calculating academic grades
 * from raw scores, decoupling evaluation policy from domain entities.
 */
public interface GradingStrategy {
    GradingResult calculateGrade(BigDecimal marksObtained, BigDecimal maxMarks);
    String getStrategyName();
}
