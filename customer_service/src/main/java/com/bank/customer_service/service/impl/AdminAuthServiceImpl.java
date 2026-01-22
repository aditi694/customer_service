package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.entity.AdminUser;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.AdminUserRepository;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.AdminAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        AdminUser admin = repo.findByUsername(request.getUsername())
                .orElseThrow(BusinessException::unauthorized);

        if (!encoder.matches(request.getPassword(), admin.getPassword())) {
            throw BusinessException.unauthorized();
        }

        // 🔥 FIX HERE
        String token = jwtUtil.generate(
                admin.getId(),     // ✅ UUID
                "ADMIN"            // ✅ ROLE
        );

        return new AdminLoginResponse(token);
    }

}
