package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.service.CustomerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class CustomerRegistrationController {

    private final CustomerRegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<CustomerRegistrationResponse>> register(
            @Valid @RequestBody CustomerRegistrationRequest request
    ) {
        CustomerRegistrationResponse data = registrationService.registerCustomer(request);
        return ResponseEntity.ok(new BaseResponse<>(
                data,
                AppConstants.REGISTRATION_SUCCESS,
                AppConstants.SUCCESS_CODE
        ));
    }
}