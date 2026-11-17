package com.campusconnect.repository;

import com.campusconnect.entity.AttendanceRecord;
import com.campusconnect.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findBySessionId(Long sessionId);
    List<AttendanceRecord> findByStudentId(Long studentId);
    Optional<AttendanceRecord> findBySessionIdAndStudentId(Long sessionId, Long studentId);

    @Query("SELECT r FROM AttendanceRecord r WHERE r.session.course.id = :courseId AND r.student.id = :studentId")
    List<AttendanceRecord> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Query("SELECT COUNT(r) FROM AttendanceRecord r WHERE r.session.course.id = :courseId AND r.student.id = :studentId")
    long countTotalByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Query("SELECT COUNT(r) FROM AttendanceRecord r WHERE r.session.course.id = :courseId AND r.student.id = :studentId AND (r.status = 'PRESENT' OR r.status = 'LATE')")
    long countAttendedByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
