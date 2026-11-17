package com.campusconnect.entity;

import com.campusconnect.enums.Role;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * UserAccount - Encapsulates security credentials and authentication status for a Person.
 * Associated 1-to-1 with Person.
 */
@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected UserAccount() {
    }

    public UserAccount(Person person, String username, String passwordHash, Role role) {
        this.person = Objects.requireNonNull(person, "Person reference must not be null");
        this.username = Objects.requireNonNull(username, "Username must not be null").trim().toLowerCase();
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash must not be null");
        this.role = Objects.requireNonNull(role, "Role must not be null");
        this.enabled = true;
        this.createdAt = Instant.now();
    }

    // Business Methods
    public void changePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "New password hash must not be null");
    }

    public void disableAccount() {
        this.enabled = false;
    }

    public void enableAccount() {
        this.enabled = true;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
