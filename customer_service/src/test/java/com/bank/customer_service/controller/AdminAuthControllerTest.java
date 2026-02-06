package com.bank.customer_service.controller;

import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.service.AdminAuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;


public class AdminAuthControllerTest {
    @Mock
    private AdminAuthService service;

    @InjectMocks
    private AdminAuthController controller;



    @Test
    void adminLogin(){
        AdminLoginRequest request = AdminLoginRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken("");
        when()
    }
}