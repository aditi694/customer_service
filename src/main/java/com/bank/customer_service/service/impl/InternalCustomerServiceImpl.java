package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.client.CustomerDetailResponse;
import com.bank.customer_service.dto.client.CustomerSummary;
import com.bank.customer_service.dto.response.BankBranchResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
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
    private final BankBranchRepository bankBranchRepo;

    @Override
    public CustomerDetailResponse getCustomerDetails(UUID customerId) {

        Customer c = customerRepo.findById(customerId)
                .orElseThrow(BusinessException::customerNotFound);

        return CustomerDetailResponse.builder()
                .customerId(c.getId().toString())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .kycStatus(c.getKycStatus().name())
                .nomineeName(c.getNomineeName())
                .nomineeRelation(c.getNomineeRelation())
                .build();
    }

    @Override
    public CustomerSummary getCustomerSummary(UUID customerId) {

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

    @Override
    public String getIfscByAccountNumber(String accountNumber) {

        Customer c = customerRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        BusinessException.notFound("Customer not found for account"));

        return c.getIfscCode();
    }

    @Override
    public BankBranchResponse getBankBranch(String ifscCode) {

        BankBranch branch = bankBranchRepo.findByIfscCode(ifscCode)
                .orElseThrow(() ->
                        BusinessException.notFound("Bank branch not found for IFSC: " + ifscCode));

        return BankBranchResponse.builder()
                .ifscCode(branch.getIfscCode())
                .bankName(branch.getBankName())
                .branchName(branch.getBranchName())
                .city(branch.getCity())
                .address(branch.getAddress())
                .build();
    }
}