package com.bank.customer_service.controller;

import com.bank.customer_service.dto.response.ApiSuccessResponse;
import com.bank.customer_service.dto.request.CustomerCreateRequest;
import com.bank.customer_service.dto.response.CustomerResponse;
import com.bank.customer_service.dto.request.KycVerifyRequest;
import com.bank.customer_service.enums.CustomerBlockReason;
import com.bank.customer_service.enums.CustomerCloseReason;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request
    ) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable UUID id
    ) {
        CustomerResponse response = customerService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(required = false) CustomerStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(customerService.getCustomers(status, pageable));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<ApiSuccessResponse> blockCustomer(
            @PathVariable UUID id,
            @RequestParam CustomerBlockReason reason
    ) {
        return ResponseEntity.ok(
                customerService.blockCustomer(id, reason)
        );
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiSuccessResponse> closeCustomer(
            @PathVariable UUID id,
            @RequestParam CustomerCloseReason reason
    ) {
        return ResponseEntity.ok(
                customerService.closeCustomer(id, reason)
        );
    }

    @PutMapping("/{id}/kyc/verify")
    public ResponseEntity<ApiSuccessResponse> verifyKyc(
            @PathVariable UUID id,
            @Valid @RequestBody KycVerifyRequest request
    ) {
        return ResponseEntity.ok(
                customerService.verifyKyc(id, request)
        );
    }

}
