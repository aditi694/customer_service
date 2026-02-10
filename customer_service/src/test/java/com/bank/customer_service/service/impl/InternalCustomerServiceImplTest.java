package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.CustomerDetailResponse;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalCustomerServiceImplTest {
    @InjectMocks
    private InternalCustomerServiceImpl service;
    @Mock
    private CustomerRepository repo;

    @Test
    void getCustomerDetailsById_success() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .phone("1234567899")
                .kycStatus(KycStatus.valueOf("APPROVED"))
                .nomineeName("Father")
                .nomineeRelation("FATHER")
                .build();

        when(repo.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDetailResponse response = service.getCustomerDetails(customerId);
        assertEquals(customerId.toString(), response.getCustomerId());
        assertEquals("Aditi Goel", response.getFullName());
        assertEquals("aditi@test.com", response.getEmail());
        assertEquals("1234567899", response.getPhone());
        assertEquals("APPROVED", response.getKycStatus());
        assertEquals("Father", response.getNomineeName());

        verify(repo).findById(customerId);
    }

    @Test
    void getCustomerDetails_customerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(repo.findById(customerId))
        .thenReturn(Optional.empty());
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.getCustomerDetails(customerId)
        );

        assertEquals("Customer not found", ex.getMessage());

        verify(repo).findById(customerId);

    }
}