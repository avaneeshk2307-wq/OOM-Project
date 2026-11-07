package com.campusconnect.event;

import com.campusconnect.enums.NotificationType;
import com.campusconnect.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AcademicEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AcademicEventListener academicEventListener;

    @Test
    @DisplayName("Observer creates notification when GradePublishedEvent is published")
    void shouldHandleGradePublishedEvent() {
        GradePublishedEvent event = new GradePublishedEvent(
                10L, 1L, "CS101", "Midterm Exam",
                BigDecimal.valueOf(88), BigDecimal.valueOf(100), "A"
        );

        academicEventListener.onGradePublished(event);

        verify(notificationService).createNotificationForPerson(
                eq(10L),
                contains("CS101"),
                contains("88"),
                eq(NotificationType.GRADE_ALERT)
        );
    }

    @Test
    @DisplayName("Observer creates notification when AttendanceShortageEvent is published")
    void shouldHandleAttendanceShortageEvent() {
        AttendanceShortageEvent event = new AttendanceShortageEvent(
                10L, 1L, "CS101", 1, 2, 50.0
        );

        academicEventListener.onAttendanceShortage(event);

        verify(notificationService).createNotificationForPerson(
                eq(10L),
                contains("CS101"),
                contains("50.0%"),
                eq(NotificationType.ATTENDANCE_SHORTAGE)
        );
    }

    @Test
    @DisplayName("Observer creates notification when EnrollmentApprovedEvent is published")
    void shouldHandleEnrollmentApprovedEvent() {
        EnrollmentApprovedEvent event = new EnrollmentApprovedEvent(
                10L, 1L, "CS101", "OOP with Java"
        );

        academicEventListener.onEnrollmentApproved(event);

        verify(notificationService).createNotificationForPerson(
                eq(10L),
                contains("CS101"),
                contains("OOP with Java"),
                eq(NotificationType.ENROLLMENT_UPDATE)
        );
    }
}
