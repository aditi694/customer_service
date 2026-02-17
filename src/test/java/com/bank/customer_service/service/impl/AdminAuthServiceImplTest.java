package com.bank.customer_service.service.impl;
import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.entity.AdminUser;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.AdminUserRepository;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.impl.AdminAuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminAuthServiceImplTest {

    @Mock
    private AdminUserRepository repo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AdminAuthServiceImpl service;

    private AdminUser admin;
    private AdminLoginRequest request;

    @BeforeEach
    void setUp() {

        admin = AdminUser.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("encodedPassword")
                .build();

        request = AdminLoginRequest.builder()
                .username("admin")
                .password("admin123")
                .build();
    }
    @Test
    void login_success() {

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(encoder.matches("admin123", "encodedPassword"))
                .thenReturn(true);

        when(jwtUtil.generate(admin.getId(), "ADMIN"))
                .thenReturn("jwt-token");

        AdminLoginResponse response = service.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(repo, times(1)).findByUsername("admin");
        verify(encoder, times(1))
                .matches("admin123", "encodedPassword");
        verify(jwtUtil, times(1))
                .generate(admin.getId(), "ADMIN");
    }
    @Test
    void login_usernameNotFound() {

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.login(request)
        );

        assertTrue(ex.getMessage()
                .contains("Invalid username or password"));

        verify(repo, times(1)).findByUsername("admin");
        verify(encoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generate(any(), any());
    }
    @Test
    void login_passwordMismatch() {

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(encoder.matches("admin123", "encodedPassword"))
                .thenReturn(false);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.login(request)
        );

        assertTrue(ex.getMessage()
                .contains("Invalid username or password"));

        verify(jwtUtil, never()).generate(any(), any());
    }

}
