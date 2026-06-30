package com.omiicare.qa.security;

import com.omiicare.qa.security.domain.RoleEntity;
import com.omiicare.qa.security.domain.UserEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security adapter over {@link UserEntity}. Exposes roles as
 * {@code ROLE_<code>} authorities and permissions as their raw codes (e.g.
 * {@code patient:read}), enabling both role- and permission-based authorization.
 * Carries the tenant id so the authentication filter can seed the tenant context.
 */
public class AppUserPrincipal implements UserDetails {

    private final String username;
    private final String passwordHash;
    private final Long tenantId;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final List<GrantedAuthority> authorities;

    public AppUserPrincipal(UserEntity user) {
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.tenantId = user.getTenantId();
        this.enabled = "ACTIVE".equals(user.getStatus());
        this.accountNonLocked = !user.isLocked();
        this.authorities = buildAuthorities(user);
    }

    private static List<GrantedAuthority> buildAuthorities(UserEntity user) {
        List<GrantedAuthority> result = new ArrayList<>();
        for (RoleEntity role : user.getRoles()) {
            result.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            role.getPermissions()
                    .forEach(p -> result.add(new SimpleGrantedAuthority(p.getCode())));
        }
        return result;
    }

    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
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
        return accountNonLocked;
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
