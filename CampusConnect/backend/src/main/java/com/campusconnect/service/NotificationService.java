package com.campusconnect.service;

import com.campusconnect.dto.NotificationResponse;
import com.campusconnect.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotificationForPerson(Long personId, String title, String message, NotificationType type);
    List<NotificationResponse> getNotificationsForUser(String username);
    long getUnreadCount(String username);
    void markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
}
