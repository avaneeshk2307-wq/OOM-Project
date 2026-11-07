package com.campusconnect.entity;

import com.campusconnect.enums.RoomType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a physical campus learning facility.
 * Demonstrates Encapsulation and Domain Invariant Validation.
 */
@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true, length = 50)
    private String roomNumber;

    @Column(name = "building", nullable = false, length = 100)
    private String building;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 50)
    private RoomType roomType = RoomType.LECTURE_HALL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Classroom() {
        // Required by JPA
    }

    public Classroom(String roomNumber, String building, int capacity, RoomType roomType) {
        setRoomNumber(roomNumber);
        setBuilding(building);
        setCapacity(capacity);
        this.roomType = roomType != null ? roomType : RoomType.LECTURE_HALL;
        this.createdAt = Instant.now();
    }

    public boolean canAccommodate(int expectedStudents) {
        return this.capacity >= expectedStudents;
    }

    public Long getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Classroom room number cannot be null or empty");
        }
        this.roomNumber = roomNumber.trim().toUpperCase();
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        if (building == null || building.trim().isEmpty()) {
            throw new IllegalArgumentException("Building name cannot be null or empty");
        }
        this.building = building.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Classroom capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        this.roomType = roomType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classroom classroom)) return false;
        return Objects.equals(roomNumber, classroom.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", building='" + building + '\'' +
                ", capacity=" + capacity +
                ", roomType=" + roomType +
                '}';
    }
}
