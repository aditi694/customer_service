package com.bank.customer_service.controller;

import com.bank.customer_service.dto.client.CustomerDetailResponse;
import com.bank.customer_service.dto.client.CustomerSummary;
import com.bank.customer_service.dto.response.BankBranchResponse;
import com.bank.customer_service.service.InternalCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/internal/customers")
@RequiredArgsConstructor
public class InternalCustomerController {

    private final InternalCustomerService service;

    @GetMapping("/{customerId}/detail")
    public CustomerDetailResponse getCustomerDetail(
            @PathVariable UUID customerId
    ) {
        return service.getCustomerDetails(customerId);
    }

    @GetMapping("/{customerId}/summary")
    public CustomerSummary getCustomerSummary(
            @PathVariable UUID customerId
    ) {
        return service.getCustomerSummary(customerId);
    }

    @GetMapping("/account/{accountNumber}/ifsc")
    public String getIfscByAccount(
            @PathVariable String accountNumber
    ) {
        return service.getIfscByAccountNumber(accountNumber);
    }
    @GetMapping("/bank-branch/{ifscCode}")
    public BankBranchResponse getBankBranch(
            @PathVariable String ifscCode
    ) {
        return service.getBankBranch(ifscCode);
    }
}