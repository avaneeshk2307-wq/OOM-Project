package com.campusconnect.entity;

import com.campusconnect.enums.AccountStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Person - Abstract base entity in the JPA JOINED inheritance hierarchy.
 * Demonstrates:
 * 1. Abstraction - Models common identity and lifecycle across all campus members.
 * 2. Inheritance - Extended by Student, Faculty, and Admin.
 * 3. Encapsulation - Enforces non-null state invariants and controlled mutators.
 */
@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Protected default constructor for JPA
    protected Person() {
    }

    // Controlled constructor for domain instantiation
    protected Person(String name, String email, String phone) {
        this.name = Objects.requireNonNull(name, "Person name must not be null");
        this.email = Objects.requireNonNull(email, "Person email must not be null");
        this.phone = phone;
        this.accountStatus = AccountStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    // Domain methods enforcing encapsulation
    public void activateAccount() {
        this.accountStatus = AccountStatus.ACTIVE;
    }

    public void suspendAccount() {
        this.accountStatus = AccountStatus.SUSPENDED;
    }

    public void deactivateAccount() {
        this.accountStatus = AccountStatus.DEACTIVATED;
    }

    public void updateContactInformation(String newName, String newPhone) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
        }
        this.phone = newPhone;
    }

    // Getters (read-only access to encapsulate internal state)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
