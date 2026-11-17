package com.campusconnect.repository;

import com.campusconnect.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    List<Course> findByDepartmentId(Long departmentId);
    List<Course> findByInstructorId(Long instructorId);
    List<Course> findBySemester(int semester);
}
