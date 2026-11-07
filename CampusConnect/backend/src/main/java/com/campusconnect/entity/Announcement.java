package com.campusconnect.entity;

import com.campusconnect.enums.Role;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Announcement - Campus-wide or course-specific broadcast message.
 * Demonstrates:
 * 1. Association - Optional link to Course or Author (Person).
 * 2. Encapsulation - Validates non-empty title/content and target audience.
 */
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", length = 50)
    private Role targetRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Announcement() {
    }

    public Announcement(String title, String content, Role targetRole, Course course, Person author) {
        this.title = Objects.requireNonNull(title, "Announcement title must not be null").trim();
        this.content = Objects.requireNonNull(content, "Announcement content must not be null").trim();
        this.targetRole = targetRole;
        this.course = course;
        this.author = author;
        this.createdAt = Instant.now();
    }

    public void updateContent(String title, String content, Role targetRole) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content.trim();
        }
        this.targetRole = targetRole;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Role getTargetRole() {
        return targetRole;
    }

    public Course getCourse() {
        return course;
    }

    public Person getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
