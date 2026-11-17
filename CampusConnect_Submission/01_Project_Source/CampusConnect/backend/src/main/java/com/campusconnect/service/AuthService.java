package com.campusconnect.service;

import com.campusconnect.dto.LoginRequest;
import com.campusconnect.dto.LoginResponse;
import com.campusconnect.dto.UserProfileResponse;

public interface AuthService {
    LoginResponse authenticate(LoginRequest loginRequest);
    UserProfileResponse getCurrentUserProfile(String username);
}
