package com.campusconnect.entity;

import com.campusconnect.enums.NotificationType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a persistent notification for a campus user.
 * Demonstrates Observer Pattern recipient state and Encapsulation.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Notification() {
        // JPA
    }

    public Notification(UserAccount user, String title, String message, NotificationType type) {
        setUser(user);
        setTitle(title);
        setMessage(message);
        this.type = type != null ? type : NotificationType.SYSTEM;
        this.isRead = false;
        this.createdAt = Instant.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        if (user == null) {
            throw new IllegalArgumentException("Notification recipient user cannot be null");
        }
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title cannot be null or empty");
        }
        this.title = title.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be null or empty");
        }
        this.message = message.trim();
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

