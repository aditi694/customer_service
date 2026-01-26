package com.bank.customer_service.controller;

import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.service.CustomerRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class CustomerRegistrationController {

    private final CustomerRegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> register(
            @RequestBody CustomerRegistrationRequest request
    ) {
        return ResponseEntity.ok(registrationService.registerCustomer(request));
    }
}