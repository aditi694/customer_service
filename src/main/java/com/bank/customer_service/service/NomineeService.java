package com.bank.customer_service.service;

import com.bank.customer_service.dto.NomineeDto;

import java.util.UUID;

public interface NomineeService {
    NomineeDto getByCustomerId(UUID customerId);
}
