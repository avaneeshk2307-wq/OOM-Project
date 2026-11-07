package com.campusconnect.event;

import com.campusconnect.enums.NotificationType;
import com.campusconnect.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * AcademicEventListener - Concrete Observer in the GoF Observer Pattern.
 * Listens to domain events published across the application and dispatches persistent notifications.
 * Demonstrates Loose Coupling and Event-Driven Architecture.
 */
@Component
public class AcademicEventListener {

    private static final Logger log = LoggerFactory.getLogger(AcademicEventListener.class);

    private final NotificationService notificationService;

    public AcademicEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onGradePublished(GradePublishedEvent event) {
        log.info("Observer caught GradePublishedEvent for studentId: {}, course: {}", event.studentId(), event.courseCode());
        String title = "Grade Published: " + event.courseCode();
        String message = String.format("Your score for %s has been evaluated: %s marks (Grade %s).",
                event.assessmentName(), event.marksObtained(), event.letterGrade());

        notificationService.createNotificationForPerson(event.studentId(), title, message, NotificationType.GRADE_ALERT);
    }

    @EventListener
    public void onAttendanceShortage(AttendanceShortageEvent event) {
        log.warn("Observer caught AttendanceShortageEvent for studentId: {}, course: {}, percentage: {}%",
                event.studentId(), event.courseCode(), event.attendancePercentage());
        String title = "Attendance Alert: " + event.courseCode();
        String message = String.format("Attendance shortage warning: Your attendance is currently %.1f%% (%d/%d sessions attended), which is below the mandatory 75%% university threshold.",
                event.attendancePercentage(), event.attendedSessions(), event.totalSessions());

        notificationService.createNotificationForPerson(event.studentId(), title, message, NotificationType.ATTENDANCE_SHORTAGE);
    }

    @EventListener
    public void onEnrollmentApproved(EnrollmentApprovedEvent event) {
        log.info("Observer caught EnrollmentApprovedEvent for studentId: {}, course: {}", event.studentId(), event.courseCode());
        String title = "Course Enrollment Approved: " + event.courseCode();
        String message = String.format("Your course registration for %s (%s) has been officially approved by the Academic Registrar.",
                event.courseCode(), event.courseTitle());

        notificationService.createNotificationForPerson(event.studentId(), title, message, NotificationType.ENROLLMENT_UPDATE);
    }
}
