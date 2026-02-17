package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.service.CustomerRegistrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationControllerTest {

    @Mock
    private CustomerRegistrationService registrationService;

    @InjectMocks
    private CustomerRegistrationController controller;

    @Test
    void register_success() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        CustomerRegistrationResponse responseDto = new CustomerRegistrationResponse();

        when(registrationService.registerCustomer(request))
                .thenReturn(responseDto);

        ResponseEntity<BaseResponse<CustomerRegistrationResponse>> response =
                controller.register(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(AppConstants.REGISTRATION_SUCCESS,
                response.getBody().getResultInfo().getResultMsg());

        verify(registrationService).registerCustomer(request);
    }
}
