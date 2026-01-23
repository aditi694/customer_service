package com.bank.customer_service.controller;

import com.bank.customer_service.dto.client.CustomerDetailResponse;
import com.bank.customer_service.dto.client.CustomerSummary;
import com.bank.customer_service.dto.response.BankBranchResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.InternalCustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/internal/customers")
public class InternalCustomerController {

    private final InternalCustomerService service;
    private final CustomerRepository customerRepo;
    private final BankBranchRepository bankBranchRepo;

    public InternalCustomerController(
            InternalCustomerService service,
            CustomerRepository customerRepo, BankBranchRepository bankBranchRepo
    ) {
        this.service = service;
        this.customerRepo = customerRepo;
        this.bankBranchRepo = bankBranchRepo;
    }

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
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(BusinessException::customerNotFound);

        return CustomerSummary.builder()
                .customerId(c.getId())
                .fullName(c.getFullName())
                .kycStatus(c.getKycStatus().name())
                .nomineeName(c.getNomineeName())
                .nomineeRelation(c.getNomineeRelation())
                .build();
    }
    @GetMapping("/{customerId}/contact")
    public String getCustomerContact(
            @PathVariable UUID customerId
    ) {
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(BusinessException::customerNotFound);

        return c.getPhone();
    }
    @GetMapping("/account/{accountNumber}/ifsc")
    public String getIfscByAccount(
            @PathVariable String accountNumber
    ) {
        Customer c = customerRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        BusinessException.notFound("Customer not found for account")
                );
        return c.getIfscCode();
    }

    @GetMapping("/bank-branch/{ifscCode}")
    public BankBranchResponse getBankBranch(
            @PathVariable String ifscCode
    ) {
        BankBranch branch = bankBranchRepo.findByIfscCode(ifscCode)
                .orElseThrow(() ->
                        BusinessException.notFound("Bank branch not found")
                );

        return BankBranchResponse.builder()
                .ifscCode(branch.getIfscCode())
                .bankName(branch.getBankName())
                .branchName(branch.getBranchName())
                .city(branch.getCity())
                .address(branch.getBranchName() + ", " + branch.getCity())
                .build();
    }

}
