package com.bank.customer_service.service;

import com.bank.customer_service.dto.request.RegisterRequest;
import com.bank.customer_service.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse registerCustomer(RegisterRequest request);
}
