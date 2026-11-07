package com.campusconnect.service;

import com.campusconnect.dto.AnnouncementResponse;
import com.campusconnect.dto.CreateAnnouncementRequest;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request, String username);
    List<AnnouncementResponse> getAnnouncementsForUser(String username);
    List<AnnouncementResponse> getAllAnnouncements();
    void deleteAnnouncement(Long id, String username);
}
