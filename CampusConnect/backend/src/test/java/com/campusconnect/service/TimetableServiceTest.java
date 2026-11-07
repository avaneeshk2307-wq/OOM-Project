package com.campusconnect.service;

import com.campusconnect.dto.CreateTimetableRequest;
import com.campusconnect.dto.TimetableResponse;
import com.campusconnect.entity.*;
import com.campusconnect.enums.RoomType;
import com.campusconnect.exception.TimetableConflictException;
import com.campusconnect.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock
    private TimetableRepository timetableRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private TimetableService timetableService;

    private Department csDept;
    private Faculty faculty;
    private Course course;
    private Classroom classroom;

    @BeforeEach
    void setUp() {
        csDept = new Department("CS", "Computer Science", "CS Dept");
        faculty = new Faculty("Dr. Alan Turing", "turing@campus.edu", "555-0101", "FAC-001", "Professor", csDept);
        course = new Course("CS101", "OOP with Java", "Intro OOP", 4, 1, 40, csDept, faculty);
        classroom = new Classroom("LH-101", "Turing Complex", 60, RoomType.LECTURE_HALL);
    }

    @Test
    @DisplayName("Successfully schedule timetable entry when no conflicts exist")
    void shouldScheduleTimetableEntrySuccessfully() {
        CreateTimetableRequest request = new CreateTimetableRequest(
                1L, 1L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        when(timetableRepository.findClassroomConflicts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(timetableRepository.findFacultyConflicts(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(timetableRepository.findCourseConflicts(any(), any(), any(), any())).thenReturn(Collections.emptyList());

        TimetableEntry savedEntry = new TimetableEntry(course, classroom, faculty, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30));
        when(timetableRepository.save(any(TimetableEntry.class))).thenReturn(savedEntry);

        TimetableResponse response = timetableService.createTimetableEntry(request);

        assertThat(response).isNotNull();
        assertThat(response.courseCode()).isEqualTo("CS101");
        assertThat(response.roomNumber()).isEqualTo("LH-101");
        assertThat(response.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        verify(timetableRepository, times(1)).save(any(TimetableEntry.class));
    }

    @Test
    @DisplayName("Throw TimetableConflictException when classroom is already booked at overlapping time")
    void shouldRejectWhenClassroomHasConflict() {
        CreateTimetableRequest request = new CreateTimetableRequest(
                1L, 1L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        TimetableEntry existing = new TimetableEntry(course, classroom, faculty, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        when(timetableRepository.findClassroomConflicts(any(), any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> timetableService.createTimetableEntry(request))
                .isInstanceOf(TimetableConflictException.class)
                .hasMessageContaining("Classroom LH-101 is already occupied");

        verify(timetableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throw TimetableConflictException when faculty is already scheduled to teach elsewhere")
    void shouldRejectWhenFacultyHasConflict() {
        CreateTimetableRequest request = new CreateTimetableRequest(
                1L, 1L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        when(timetableRepository.findClassroomConflicts(any(), any(), any(), any())).thenReturn(Collections.emptyList());

        TimetableEntry existing = new TimetableEntry(course, classroom, faculty, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
        when(timetableRepository.findFacultyConflicts(any(), any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> timetableService.createTimetableEntry(request))
                .isInstanceOf(TimetableConflictException.class)
                .hasMessageContaining("already scheduled to teach");

        verify(timetableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reject scheduling when classroom capacity is smaller than course capacity")
    void shouldRejectWhenRoomCapacityIsInsufficient() {
        Classroom smallRoom = new Classroom("SR-10", "Small Hall", 20, RoomType.SEMINAR_ROOM);
        CreateTimetableRequest request = new CreateTimetableRequest(
                1L, 2L, 1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course)); // capacity 40
        when(classroomRepository.findById(2L)).thenReturn(Optional.of(smallRoom)); // capacity 20
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        assertThatThrownBy(() -> timetableService.createTimetableEntry(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot accommodate");

        verify(timetableRepository, never()).save(any());
    }

    @Test
    @DisplayName("Verify interval overlap mathematics on TimetableEntry")
    void shouldCorrectlyDetectIntervalOverlaps() {
        TimetableEntry entry = new TimetableEntry(course, classroom, faculty,
                DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Different day -> false
        assertThat(entry.overlapsWith(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))).isFalse();

        // Exact match -> true
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))).isTrue();

        // Partial overlap starting before -> true (09:30 to 10:30)
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30))).isTrue();

        // Partial overlap ending after -> true (11:30 to 12:30)
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(11, 30), LocalTime.of(12, 30))).isTrue();

        // Enclosed inside -> true (10:30 to 11:30)
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30))).isTrue();

        // Strictly before (08:00 to 10:00) -> false (boundary touching does not overlap)
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))).isFalse();

        // Strictly after (12:00 to 14:00) -> false (boundary touching does not overlap)
        assertThat(entry.overlapsWith(DayOfWeek.TUESDAY, LocalTime.of(12, 0), LocalTime.of(14, 0))).isFalse();
    }
}
