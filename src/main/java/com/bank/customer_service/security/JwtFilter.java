package com.bank.customer_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils; // Import this

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        if (path.startsWith("/api/public/register") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/internal/") ||
                path.startsWith("/customers/")) {
            chain.doFilter(req, res);
            return;
        }

        // 2. Extract token from Cookie using WebUtils (No loop needed)
        String token = null;
        Cookie cookie = WebUtils.getCookie(req, "token");
        System.out.println("COOKIE: " + (cookie != null ? cookie.getValue() : "NULL"));
        if (cookie != null) {
            token = cookie.getValue();
            logger.info("Found token in cookie: " + token);
        } else {
            String header = req.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
        }

        if (token == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"message\":\"Authorization token is missing\"}");
            return;
        }

        try {
            Claims claims = jwtUtil.parse(token);

            String customerId = claims.get("customerId", String.class);
            String role = claims.get("role", String.class);

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            System.out.println("✅ Customer Service Auth: " + role + " | Customer: " + customerId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            customerId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);

        } catch (JwtException | IllegalArgumentException ex) {
            System.err.println("❌ Customer Service JWT Auth Failed: " + ex.getMessage());
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"message\":\"Invalid or expired token\",\"status\":401}");
        }
    }
}
