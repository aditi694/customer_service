package com.bank.customer_service.controller;

import com.bank.customer_service.dto.client.CustomerDetailResponse;
import com.bank.customer_service.dto.client.CustomerSummary;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.InternalCustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/internal/customers")
public class InternalCustomerController {

    private final InternalCustomerService service;
    private final CustomerRepository customerRepo;

    public InternalCustomerController(
            InternalCustomerService service,
            CustomerRepository customerRepo
    ) {
        this.service = service;
        this.customerRepo = customerRepo;
    }

    // 🔹 DETAILED CUSTOMER (admin / internal heavy)
    @GetMapping("/{customerId}/detail")
    public CustomerDetailResponse getCustomerDetail(
            @PathVariable UUID customerId
    ) {
        return service.getCustomerDetails(customerId);
    }

    // 🔹 SUMMARY CUSTOMER (dashboard lightweight)
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
}
