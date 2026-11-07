package com.campusconnect.repository;

import com.campusconnect.entity.Announcement;
import com.campusconnect.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByCreatedAtDesc();
    List<Announcement> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    @Query("SELECT a FROM Announcement a WHERE a.targetRole IS NULL OR a.targetRole = :role ORDER BY a.createdAt DESC")
    List<Announcement> findAnnouncementsForRole(@Param("role") Role role);
}
