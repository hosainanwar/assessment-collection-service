package com.nhimex.assessment_collection.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Long pourashavaId;
    private final String subdomain;
    private final List<String> roleCodes;
    private final List<String> permissionCodes;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public boolean isSuperAdmin() {
        return roleCodes != null && roleCodes.contains(RoleCodes.SUPER_ADMIN);
    }

    public boolean matchesTenant(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return false;
        }
        String trimmed = tenantId.trim();
        if (subdomain != null && subdomain.equalsIgnoreCase(trimmed)) {
            return true;
        }
        return pourashavaId != null && String.valueOf(pourashavaId).equals(trimmed);
    }

    public boolean hasPermission(String code) {
        return isSuperAdmin() || (permissionCodes != null && permissionCodes.contains(code));
    }

    public Set<String> permissionSet() {
        return permissionCodes == null ? Set.of() : Set.copyOf(permissionCodes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
