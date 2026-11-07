package com.campusconnect.patterns.builder;

import com.campusconnect.entity.Student;
import java.math.BigDecimal;

/**
 * GoF Builder Pattern interface.
 * Defines the abstract steps to construct parts of a complex AcademicTranscriptReport.
 */
public interface TranscriptBuilder {
    TranscriptBuilder buildHeader(String universityName, String documentTitle);
    TranscriptBuilder buildStudentBio(Student student);
    TranscriptBuilder addCourseGrade(int semester, String courseCode, String courseTitle,
                                     int credits, BigDecimal marksObtained, String letterGrade,
                                     double gradePoint, String remark);
    TranscriptBuilder calculateAcademicSummary();
    TranscriptBuilder buildSignatures(String registrarName, String officialSeal);
    AcademicTranscriptReport build();
}
