package com.campusconnect.repository;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, int semester);
    boolean existsByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, int semester);
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
}
