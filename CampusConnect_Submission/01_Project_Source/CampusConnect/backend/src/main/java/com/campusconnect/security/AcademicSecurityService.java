package com.campusconnect.security;

import com.campusconnect.enums.Role;
import com.campusconnect.repository.EnrollmentRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AcademicSecurityService - Centralized authorization evaluator for student academic records.
 * Mitigates Insecure Direct Object Reference (IDOR) vulnerabilities across enrollment,
 * attendance, and grade inspection endpoints.
 */
@Service("academicSecurityService")
public class AcademicSecurityService {

    private static final Logger log = LoggerFactory.getLogger(AcademicSecurityService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userAccountRepository;

    public AcademicSecurityService(EnrollmentRepository enrollmentRepository, UserAccountRepository userAccountRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Checks if the currently authenticated principal is authorized to access
     * the academic records of the specified student.
     *
     * Rules:
     * 1. ROLE_ADMIN: Always authorized.
     * 2. ROLE_STUDENT: Authorized ONLY if the authenticated student matches the target studentId.
     * 3. ROLE_FACULTY: Authorized ONLY if the faculty member teaches a course in which the target student is enrolled.
     *
     * @param targetStudentId Target student database ID.
     * @return true if authorized, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean canAccessStudentRecord(Long targetStudentId) {
        if (targetStudentId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        // 1. Admin access: Always permitted
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return true;
        }

        String username = authentication.getName();
        return userAccountRepository.findByUsername(username)
                .map(account -> {
                    Long personId = account.getPerson().getId();

                    // 2. Student access: Self-only verification
                    if (account.getRole() == Role.ROLE_STUDENT) {
                        boolean isSelf = personId.equals(targetStudentId);
                        if (!isSelf) {
                            log.warn("IDOR attempt blocked: Student user '{}' (ID: {}) attempted to access student ID {}",
                                    username, personId, targetStudentId);
                        }
                        return isSelf;
                    }

                    // 3. Faculty access: Course instructor validation
                    if (account.getRole() == Role.ROLE_FACULTY) {
                        boolean isTeaching = enrollmentRepository.isFacultyTeachingStudent(personId, targetStudentId);
                        if (!isTeaching) {
                            log.warn("IDOR attempt blocked: Faculty user '{}' (ID: {}) attempted to access unassigned student ID {}",
                                    username, personId, targetStudentId);
                        }
                        return isTeaching;
                    }

                    return false;
                })
                .orElse(false);
    }
}
