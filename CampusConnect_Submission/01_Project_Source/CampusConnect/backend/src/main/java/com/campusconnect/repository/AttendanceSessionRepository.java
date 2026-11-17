package com.campusconnect.repository;

import com.campusconnect.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findByCourseId(Long courseId);
    List<AttendanceSession> findByCourseIdOrderBySessionDateDesc(Long courseId);
}
