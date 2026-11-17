package com.campusconnect.security;

import com.campusconnect.entity.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * SecurityUser - Adapter pattern implementing Spring Security's UserDetails interface
 * wrapping the domain UserAccount entity.
 */
public class SecurityUser implements UserDetails {

    private final Long userId;
    private final Long personId;
    private final String username;
    private final String password;
    private final String fullName;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public SecurityUser(UserAccount account) {
        this.userId = account.getId();
        this.personId = account.getPerson().getId();
        this.username = account.getUsername();
        this.password = account.getPasswordHash();
        this.fullName = account.getPerson().getName();
        this.email = account.getPerson().getEmail();
        this.authorities = List.of(new SimpleGrantedAuthority(account.getRole().name()));
        this.enabled = account.isEnabled();
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
