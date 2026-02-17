package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerDetailResponse;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.InternalCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InternalCustomerServiceImpl implements InternalCustomerService {

    private final CustomerRepository customerRepo;

    @Override
    public CustomerDetailResponse getCustomerDetails(UUID customerId) {

        System.out.println("=== FETCHING CUSTOMER DETAILS ===");
        System.out.println("Customer ID: " + customerId);

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> BusinessException.notFound("Customer not found"));

        return CustomerDetailResponse.builder()
                .customerId(customer.getId().toString())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .kycStatus(customer.getKycStatus().name())
                .nomineeName(customer.getNomineeName())
                .nomineeRelation(customer.getNomineeRelation())
                .build();
    }
}