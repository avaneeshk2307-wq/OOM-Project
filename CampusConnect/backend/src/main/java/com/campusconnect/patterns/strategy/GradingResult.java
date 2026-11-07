package com.campusconnect.patterns.strategy;

import com.campusconnect.enums.GradeLetter;

public record GradingResult(
        GradeLetter gradeLetter,
        double gradePoint,
        double percentage,
        String remark
) {}
