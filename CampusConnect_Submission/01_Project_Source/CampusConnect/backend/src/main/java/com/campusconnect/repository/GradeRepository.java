package com.campusconnect.repository;

import com.campusconnect.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByEnrollmentId(Long enrollmentId);
    List<Grade> findByEnrollmentStudentId(Long studentId);
    List<Grade> findByAssessmentCourseId(Long courseId);
    Optional<Grade> findByEnrollmentIdAndAssessmentId(Long enrollmentId, Long assessmentId);
}
