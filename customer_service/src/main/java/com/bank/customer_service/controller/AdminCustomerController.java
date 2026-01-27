package com.bank.customer_service.controller;

import com.bank.customer_service.dto.client.AdminCustomerDetail;
import com.bank.customer_service.dto.client.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.ApiSuccessResponse;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.service.AdminCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final AdminCustomerService service;

    @GetMapping("/customers")
    public List<AdminCustomerSummary> getAll() {
        return service.getAllCustomers();
    }

    @GetMapping("/customer/{customerId}")
    public AdminCustomerDetail getById(@PathVariable UUID customerId) {
        return service.getCustomerById(customerId);
    }

    @PutMapping("customers/{customerId}/kyc")
    public ApiSuccessResponse<KycApprovalResponse> kyc(
            @PathVariable UUID customerId,
            @RequestBody KycApprovalRequest req
    ) {
        return new ApiSuccessResponse<>(
                "KYC status updated successfully",
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
                "Customer blocked successfully",
                service.blockCustomer(customerId, reason),
                LocalDateTime.now()
        );
    }

    @PutMapping("/{customerId}/unblock")
    public ApiSuccessResponse<AdminCustomerDetail> unblock(@PathVariable UUID customerId) {
        return new ApiSuccessResponse<>(
                "Customer unblocked successfully",
                service.unblockCustomer(customerId),
                LocalDateTime.now()
        );
    }

    @PutMapping("/{customerId}")
    public ApiSuccessResponse update(
            @PathVariable UUID customerId,
            @RequestBody UpdateCustomerRequest req
    ) {
        service.updateCustomer(customerId, req);
        return new ApiSuccessResponse(
                "Customer details updated successfully",
                null,
                LocalDateTime.now()
        );
    }

    @DeleteMapping("/{customerId}")
    public ApiSuccessResponse delete(@PathVariable UUID customerId) {
        service.deleteCustomer(customerId);
        return new ApiSuccessResponse(
                "Customer account deleted successfully",
                null,
                LocalDateTime.now()
        );
    }
}