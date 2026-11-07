package com.campusconnect.dto;

import com.campusconnect.enums.RoomType;
import java.time.Instant;

public record ClassroomResponse(
        Long id,
        String roomNumber,
        String building,
        int capacity,
        RoomType roomType,
        Instant createdAt
) {}
