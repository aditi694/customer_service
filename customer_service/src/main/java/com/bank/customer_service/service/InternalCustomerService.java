package com.bank.customer_service.service;

import com.bank.customer_service.dto.CustomerDetailResponse;

import java.util.UUID;

public interface InternalCustomerService {
    CustomerDetailResponse getCustomerDetails(UUID customerId);
}