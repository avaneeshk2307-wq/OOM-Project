package com.campusconnect.repository;

import com.campusconnect.entity.AssessmentComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentComponentRepository extends JpaRepository<AssessmentComponent, Long> {
    List<AssessmentComponent> findByCourseId(Long courseId);
}
