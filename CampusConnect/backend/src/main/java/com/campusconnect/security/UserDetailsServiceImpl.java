package com.campusconnect.security;

import com.campusconnect.entity.UserAccount;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsServiceImpl - Retrieves user authentication details from the repository.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new SecurityUser(account);
    }
}
