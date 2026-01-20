package com.bank.customer_service.controller;

import com.bank.customer_service.dto.client.AdminCustomerDetail;
import com.bank.customer_service.dto.client.AdminCustomerSummary;
import com.bank.customer_service.dto.request.CreateCustomerAccountRequest;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.ApiSuccessResponse;
import com.bank.customer_service.dto.response.CreateCustomerAccountResponse;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.service.AdminCustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminCustomerController {

    private final AdminCustomerService service;

    public AdminCustomerController(AdminCustomerService service) {
        this.service = service;
    }

    @PostMapping("/create-account")
    public ResponseEntity<CreateCustomerAccountResponse> create(
            @RequestBody CreateCustomerAccountRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    // 2️⃣ Get all customers
    @GetMapping("/customers")
    public List<AdminCustomerSummary> getAll() {
        return service.getAllCustomers();
    }

    @GetMapping("/customer/{customerId}")
    public AdminCustomerDetail getById(@PathVariable UUID customerId) {
        return service.getCustomerById(customerId);
    }

    @PutMapping("/{customerId}/kyc")
    public ApiSuccessResponse<KycApprovalResponse> kyc(
            @PathVariable UUID customerId,
            @RequestBody KycApprovalRequest req
    ) {
        return new ApiSuccessResponse<>(
                "KYC status updated",
                service.approveOrRejectKyc(customerId, req),
                LocalDateTime.now()
        );
    }

    @PutMapping("/{customerId}/block")
    public ApiSuccessResponse<AdminCustomerDetail> block(
            @PathVariable UUID customerId,
            @RequestParam String reason
    ) {
        return new ApiSuccessResponse<>(
                "Customer blocked",
                service.blockCustomer(customerId, reason),
                LocalDateTime.now()
        );
    }

    @PutMapping("/{customerId}/unblock")
    public ApiSuccessResponse<AdminCustomerDetail> unblock(@PathVariable UUID customerId) {
        return new ApiSuccessResponse<>(
                "Customer unblocked",
                service.unblockCustomer(customerId),
                LocalDateTime.now()
        );
    }

    // 7️⃣ Update customer
    @PutMapping("/{customerId}")
    public ApiSuccessResponse update(
            @PathVariable UUID customerId,
            @RequestBody UpdateCustomerRequest req
    ) {
        service.updateCustomer(customerId, req);
        return new ApiSuccessResponse(
                "Customer updated",
                null,
                LocalDateTime.now()
        );
    }

    // 8️⃣ Soft delete
    @DeleteMapping("/{customerId}")
    public ApiSuccessResponse delete(@PathVariable UUID customerId) {
        service.deleteCustomer(customerId);
        return new ApiSuccessResponse(
                "Customer deleted",
                null,
                LocalDateTime.now()
        );
    }
}