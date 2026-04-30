package com.tableorder.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user;
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static Long getCurrentStoreId() {
        return getCurrentUser().storeId();
    }

    public static Long getCurrentSubjectId() {
        return getCurrentUser().subjectId();
    }
}
