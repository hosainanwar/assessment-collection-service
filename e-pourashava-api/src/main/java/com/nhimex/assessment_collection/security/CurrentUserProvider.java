package com.nhimex.assessment_collection.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserProvider {

    private CurrentUserProvider() {
    }

    public static UserPrincipal get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal;
    }

    public static UserPrincipal require() {
        UserPrincipal principal = get();
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return principal;
    }
}
