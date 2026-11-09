package com.campusconnect.service;

import com.campusconnect.dto.NotificationResponse;
import com.campusconnect.entity.Notification;
import com.campusconnect.entity.UserAccount;
import com.campusconnect.enums.NotificationType;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.NotificationRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserAccountRepository userAccountRepository) {
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void createNotificationForPerson(Long personId, String title, String message, NotificationType type) {
        userAccountRepository.findByPersonId(personId).ifPresent(account -> {
            Notification notification = new Notification(account, title, message, type);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(account.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return notificationRepository.countByUserIdAndIsReadFalse(account.getId());
    }

    @Override
    public void markAsRead(Long notificationId, String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(account.getId())) {
            throw new IllegalArgumentException("Cannot modify notifications belonging to another user");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(account.getId())
                .stream()
                .filter(n -> !n.isRead())
                .toList();

        for (Notification n : unread) {
            n.markAsRead();
        }
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}

