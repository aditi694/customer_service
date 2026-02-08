package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.AdminAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(
        controllers = AdminAuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        }
)
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAuthService authService;

    // Needed because JwtFilter exists
    @MockBean
    private JwtUtil jwtUtil;


    @Test
    void login_success() throws Exception {

        String json = """
        {
          "username": "admin",
          "password": "admin123"
        }
        """;

        AdminLoginResponse response =
                AdminLoginResponse.builder()
                        .token("jwt")
                        .build();

        when(authService.login(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message")
                        .value(AppConstants.SUCCESS_MSG))
                .andExpect(jsonPath("$.code")
                        .value(AppConstants.SUCCESS_CODE));

        verify(authService).login(any());
    }

}
