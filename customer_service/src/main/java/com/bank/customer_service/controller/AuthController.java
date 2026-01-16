package com.bank.customer_service.controller;

import com.bank.customer_service.dto.request.LoginRequest;
import com.bank.customer_service.dto.request.RegisterRequest;
import com.bank.customer_service.dto.response.LoginResponse;
import com.bank.customer_service.dto.response.RegisterResponse;
import com.bank.customer_service.entity.UserEntity;
import com.bank.customer_service.repository.UserRepository;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔐 CUSTOMER SELF REGISTER
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.registerCustomer(request);
    }

    // 🔑 LOGIN
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, user.getRole().name());
    }
}
