package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.request.AdminLoginRequest;
import com.bank.customer_service.dto.response.AdminLoginResponse;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.service.AdminAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    @Mock
    private AdminAuthService authService;

    @InjectMocks
    private AdminAuthController controller;

    @Test
    void login_success() {

        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("password");

        AdminLoginResponse responseDto = new AdminLoginResponse();
        responseDto.setToken("jwt-token");

        when(authService.login(request)).thenReturn(responseDto);

        ResponseEntity<BaseResponse<AdminLoginResponse>> response =
                controller.login(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("jwt-token",
                response.getBody().getData().getToken());
        Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                response.getBody().getResultInfo().getResultMsg());

        verify(authService).login(request);
    }
}
