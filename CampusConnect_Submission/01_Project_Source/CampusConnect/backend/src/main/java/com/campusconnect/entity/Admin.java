package com.campusconnect.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Admin - Concrete domain model extending Person representing university administrators.
 * Demonstrates:
 * 1. Inheritance - Subclass of Person in the JOINED table hierarchy.
 * 2. Encapsulation - Controlled administrative privilege levels.
 */
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends Person {

    @Column(name = "access_level", nullable = false, length = 50)
    private String accessLevel = "SUPER_ADMIN";

    protected Admin() {
        super();
    }

    public Admin(String name, String email, String phone, String accessLevel) {
        super(name, email, phone);
        this.accessLevel = accessLevel != null ? accessLevel.trim() : "SUPER_ADMIN";
    }

    public void updateAccessLevel(String accessLevel) {
        this.accessLevel = Objects.requireNonNull(accessLevel, "Access level must not be null").trim();
    }

    public String getAccessLevel() {
        return accessLevel;
    }
}
