package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.entity.AdminUser;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.AdminUserRepository;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.AdminAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AdminAuthServiceImpl(
            AdminUserRepository repo,
            JwtUtil jwtUtil,
            PasswordEncoder encoder
    ) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {

        log.info("Admin login attempt for username: {}", request.getUsername());

        AdminUser admin = repo.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Admin not found: {}", request.getUsername());
                    return BusinessException.unauthorized("Invalid username or password");
                });

        if (!encoder.matches(request.getPassword(), admin.getPassword())) {
            log.error("Invalid password for admin: {}", request.getUsername());
            throw BusinessException.unauthorized("Invalid username or password");
        }

        // ✅ Generate token with ADMIN role (without ROLE_ prefix - filter adds it)
        String token = jwtUtil.generate(admin.getId(), "ADMIN");

        log.info("✅ Admin login successful: {} - Token generated", request.getUsername());

        return new AdminLoginResponse(token);
    }
}