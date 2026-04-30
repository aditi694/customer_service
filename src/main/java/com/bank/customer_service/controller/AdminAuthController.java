package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.service.AdminAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AdminLoginResponse>> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletResponse response
    ) {
        AdminLoginResponse data = authService.login(request);
        Cookie cookie = new Cookie("token", data.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        response.addHeader("Set-Cookie",
                "token=" + data.getToken() +
                        "; Path=/; HttpOnly; SameSite=None");
       System.out.println("cookies::::::: "+cookie);
        return ResponseEntity.ok(new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        return ResponseEntity.ok(auth.getPrincipal());
    }
}
