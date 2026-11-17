package com.campusconnect.service;

import com.campusconnect.dto.LoginRequest;
import com.campusconnect.dto.LoginResponse;
import com.campusconnect.dto.UserProfileResponse;
import com.campusconnect.entity.*;
import com.campusconnect.repository.*;
import com.campusconnect.security.JwtTokenProvider;
import com.campusconnect.security.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl - Coordinates credential verification, JWT issuance, and user profile retrieval.
 * Implements Dependency Inversion Principle (DIP) and Single Responsibility Principle (SRP).
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserAccountRepository userAccountRepository,
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            AdminRepository adminRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username().trim().toLowerCase(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        SecurityUser userPrincipal = (SecurityUser) authentication.getPrincipal();
        UserAccount account = userAccountRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User account not found"));

        return LoginResponse.of(
                jwt,
                account.getId(),
                account.getPerson().getId(),
                account.getUsername(),
                account.getPerson().getName(),
                account.getPerson().getEmail(),
                account.getRole()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String username) {
        UserAccount account = userAccountRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User account not found: " + username));

        Long personId = account.getPerson().getId();
        String code = null;
        String deptName = null;

        switch (account.getRole()) {
            case ROLE_STUDENT -> {
                Student student = studentRepository.findById(personId).orElse(null);
                if (student != null) {
                    code = student.getRollNumber();
                    deptName = student.getDepartment() != null ? student.getDepartment().getName() : null;
                }
            }
            case ROLE_FACULTY -> {
                Faculty faculty = facultyRepository.findById(personId).orElse(null);
                if (faculty != null) {
                    code = faculty.getEmployeeCode();
                    deptName = faculty.getDepartment() != null ? faculty.getDepartment().getName() : null;
                }
            }
            case ROLE_ADMIN -> {
                Admin admin = adminRepository.findById(personId).orElse(null);
                if (admin != null) {
                    code = admin.getAccessLevel();
                    deptName = "University Administration";
                }
            }
        }

        return new UserProfileResponse(
                account.getId(),
                personId,
                account.getUsername(),
                account.getPerson().getName(),
                account.getPerson().getEmail(),
                account.getRole(),
                code,
                deptName
        );
    }
}
