package com.campusconnect.repository;

import com.campusconnect.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);
    boolean existsByRollNumber(String rollNumber);
    List<Student> findByDepartmentId(Long departmentId);
    List<Student> findBySemester(int semester);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);

    @Query("SELECT s FROM Student s WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:departmentId IS NULL OR (s.department IS NOT NULL AND s.department.id = :departmentId)) AND " +
           "(:semester IS NULL OR s.semester = :semester)")
    Page<Student> findStudentsFiltered(@Param("query") String query, @Param("departmentId") Long departmentId, @Param("semester") Integer semester, Pageable pageable);
}
