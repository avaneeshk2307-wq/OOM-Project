package com.campusconnect.service;

import com.campusconnect.dto.AnnouncementResponse;
import com.campusconnect.dto.CreateAnnouncementRequest;
import com.campusconnect.entity.*;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.AnnouncementRepository;
import com.campusconnect.repository.CourseRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final UserAccountRepository userAccountRepository;

    public AnnouncementServiceImpl(
            AnnouncementRepository announcementRepository,
            CourseRepository courseRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.announcementRepository = announcementRepository;
        this.courseRepository = courseRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request, String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Course course = null;
        if (request.courseId() != null) {
            course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));
        }

        Announcement announcement = new Announcement(
                request.title(),
                request.content(),
                request.targetRole(),
                course,
                account.getPerson()
        );

        Announcement saved = announcementRepository.save(announcement);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsForUser(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (account.getRole() == Role.ROLE_ADMIN) {
            return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return announcementRepository.findAnnouncementsForRole(account.getRole()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAnnouncement(Long id, String username) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement not found with id: " + id);
        }
        announcementRepository.deleteById(id);
    }

    private AnnouncementResponse mapToResponse(Announcement a) {
        return new AnnouncementResponse(
                a.getId(),
                a.getTitle(),
                a.getContent(),
                a.getTargetRole(),
                a.getCourse() != null ? a.getCourse().getId() : null,
                a.getCourse() != null ? a.getCourse().getCode() : null,
                a.getAuthor() != null ? a.getAuthor().getName() : "Campus Administration",
                a.getCreatedAt()
        );
    }
}
