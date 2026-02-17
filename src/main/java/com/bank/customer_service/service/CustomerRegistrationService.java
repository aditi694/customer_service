package com.bank.customer_service.service;

import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;

public interface CustomerRegistrationService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request);
}