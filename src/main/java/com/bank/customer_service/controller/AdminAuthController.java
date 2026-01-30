package com.bank.customer_service.controller;

import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.service.AdminAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @RequestBody AdminLoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
