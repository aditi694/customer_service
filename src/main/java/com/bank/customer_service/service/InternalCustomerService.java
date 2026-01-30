package com.bank.customer_service.service;

import com.bank.customer_service.dto.client.CustomerDetailResponse;
import com.bank.customer_service.dto.client.CustomerSummary;
import com.bank.customer_service.dto.response.BankBranchResponse;

import java.util.UUID;

public interface InternalCustomerService {

        CustomerDetailResponse getCustomerDetails(UUID customerId);

        CustomerSummary getCustomerSummary(UUID customerId);

        String getIfscByAccountNumber(String accountNumber);

        BankBranchResponse getBankBranch(String ifscCode); // ✅ ADD
}