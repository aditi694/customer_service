package com.bank.customer_service.service;

import com.bank.customer_service.dto.response.ApiSuccessResponse;
import com.bank.customer_service.dto.request.CustomerCreateRequest;
import com.bank.customer_service.dto.response.CustomerResponse;
import com.bank.customer_service.dto.request.KycVerifyRequest;
import com.bank.customer_service.enums.CustomerBlockReason;
import com.bank.customer_service.enums.CustomerCloseReason;
import com.bank.customer_service.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);

    CustomerResponse getById(UUID id);

    Page<CustomerResponse> getCustomers(CustomerStatus status, Pageable pageable);

    ApiSuccessResponse blockCustomer(UUID id, CustomerBlockReason reason);

    ApiSuccessResponse closeCustomer(UUID id, CustomerCloseReason reason);

    ApiSuccessResponse verifyKyc(UUID customerId, KycVerifyRequest request);

}
