package com.bank.customer_service.util;

import com.bank.customer_service.security.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static AuthUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null
                || !(auth.getPrincipal() instanceof AuthUser)) {
            return null; // SYSTEM or unauthenticated
        }

        return (AuthUser) auth.getPrincipal();
    }
}
