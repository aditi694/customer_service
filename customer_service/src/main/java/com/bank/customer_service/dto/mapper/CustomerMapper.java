package com.bank.customer_service.dto.mapper;

import com.bank.customer_service.dto.response.CustomerResponse;
import com.bank.customer_service.entity.Customer;

public class CustomerMapper {

    private CustomerMapper() {}

    public static CustomerResponse toResponse(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .kycStatus(customer.getKycStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
