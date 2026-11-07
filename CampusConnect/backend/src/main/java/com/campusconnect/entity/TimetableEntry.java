package com.campusconnect.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain entity representing a scheduled lecture/lab slot in the campus master schedule.
 * Demonstrates Association, Encapsulation, and Temporal Invariant Validation.
 */
@Entity
@Table(name = "timetables")
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TimetableEntry() {
        // Required by JPA
    }

    public TimetableEntry(Course course, Classroom classroom, Faculty faculty,
                          DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        setCourse(course);
        setClassroom(classroom);
        setFaculty(faculty);
        setDayOfWeek(dayOfWeek);
        setTimeRange(startTime, endTime);
        this.createdAt = Instant.now();
    }

    /**
     * Determines whether this scheduled slot overlaps with another time window on the same day.
     * Interval intersection rule: [A.start < B.end] AND [A.end > B.start]
     */
    public boolean overlapsWith(DayOfWeek day, LocalTime otherStart, LocalTime otherEnd) {
        if (this.dayOfWeek != day) {
            return false;
        }
        return this.startTime.isBefore(otherEnd) && this.endTime.isAfter(otherStart);
    }

    public void setTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Schedule start and end time must not be null");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Schedule start time (" + startTime + ") must be before end time (" + endTime + ")");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Scheduled course cannot be null");
        }
        this.course = course;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        if (classroom == null) {
            throw new IllegalArgumentException("Assigned classroom cannot be null");
        }
        this.classroom = classroom;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new IllegalArgumentException("Assigned faculty cannot be null");
        }
        this.faculty = faculty;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week cannot be null");
        }
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimetableEntry that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
