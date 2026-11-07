package com.campusconnect.patterns.strategy;

import com.campusconnect.enums.GradingMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting GradingStrategy instances at runtime.
 * Demonstrates GoF Strategy Pattern selection and Polymorphism.
 */
@Component
public class GradingStrategyFactory {

    private final GradingStrategy absoluteStrategy;
    private final GradingStrategy relativeStrategy;

    public GradingStrategyFactory(
            @Qualifier("absoluteGradingStrategy") GradingStrategy absoluteStrategy,
            @Qualifier("relativeGradingStrategy") GradingStrategy relativeStrategy
    ) {
        this.absoluteStrategy = absoluteStrategy;
        this.relativeStrategy = relativeStrategy;
    }

    public GradingStrategy getStrategy(GradingMode mode) {
        if (mode == GradingMode.RELATIVE) {
            return relativeStrategy;
        }
        return absoluteStrategy;
    }

    public GradingStrategy getStrategyByName(String strategyName) {
        if (strategyName != null && strategyName.trim().equalsIgnoreCase("RELATIVE")) {
            return relativeStrategy;
        }
        return absoluteStrategy;
    }

    public GradingStrategy getAbsoluteStrategy() {
        return absoluteStrategy;
    }

    public GradingStrategy getRelativeStrategy() {
        return relativeStrategy;
    }
}
