package com.campusconnect.enums;

/**
 * GradeLetter - Standard academic letter grade with corresponding grade points.
 */
public enum GradeLetter {
    A_PLUS("A+", 4.0, "Outstanding"),
    A("A", 4.0, "Excellent"),
    B_PLUS("B+", 3.5, "Very Good"),
    B("B", 3.0, "Good"),
    C_PLUS("C+", 2.5, "Above Average"),
    C("C", 2.0, "Average"),
    D("D", 1.0, "Pass"),
    F("F", 0.0, "Fail");

    private final String display;
    private final double gradePoint;
    private final String description;

    GradeLetter(String display, double gradePoint, String description) {
        this.display = display;
        this.gradePoint = gradePoint;
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public String getDescription() {
        return description;
    }
}
