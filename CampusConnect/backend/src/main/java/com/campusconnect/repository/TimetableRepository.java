package com.campusconnect.repository;

import com.campusconnect.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntry, Long> {

    List<TimetableEntry> findByCourseId(Long courseId);

    List<TimetableEntry> findByFacultyId(Long facultyId);

    List<TimetableEntry> findByClassroomId(Long classroomId);

    List<TimetableEntry> findByCourseIdIn(List<Long> courseIds);

    @Query("SELECT t FROM TimetableEntry t WHERE t.classroom.id = :classroomId AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableEntry> findClassroomConflicts(@Param("classroomId") Long classroomId,
                                                @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                @Param("startTime") LocalTime startTime,
                                                @Param("endTime") LocalTime endTime);

    @Query("SELECT t FROM TimetableEntry t WHERE t.faculty.id = :facultyId AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableEntry> findFacultyConflicts(@Param("facultyId") Long facultyId,
                                              @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                              @Param("startTime") LocalTime startTime,
                                              @Param("endTime") LocalTime endTime);

    @Query("SELECT t FROM TimetableEntry t WHERE t.course.id = :courseId AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableEntry> findCourseConflicts(@Param("courseId") Long courseId,
                                             @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                             @Param("startTime") LocalTime startTime,
                                             @Param("endTime") LocalTime endTime);
}

