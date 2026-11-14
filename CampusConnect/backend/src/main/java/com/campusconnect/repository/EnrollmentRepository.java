package com.campusconnect.repository;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, int semester);
    boolean existsByStudentIdAndCourseIdAndSemester(Long studentId, Long courseId, int semester);
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    @org.springframework.data.jpa.repository.Query(
        "SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.student.id = :studentId AND e.course.instructor.id = :facultyId"
    )
    boolean isFacultyTeachingStudent(
        @org.springframework.data.repository.query.Param("facultyId") Long facultyId,
        @org.springframework.data.repository.query.Param("studentId") Long studentId
    );

    @org.springframework.data.jpa.repository.Query(
        "SELECT e FROM Enrollment e WHERE (:status IS NULL OR e.status = :status)"
    )
    Page<Enrollment> findEnrollmentsFiltered(
        @org.springframework.data.repository.query.Param("status") EnrollmentStatus status,
        Pageable pageable
    );
}
