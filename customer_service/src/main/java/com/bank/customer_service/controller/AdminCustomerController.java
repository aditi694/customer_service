package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.client.AdminCustomerDetail;
import com.bank.customer_service.dto.client.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.service.AdminCustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public BaseResponse<List<AdminCustomerSummary>> getAll() {
        List<AdminCustomerSummary> data = service.getAllCustomers();
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @GetMapping("/customer/{customerId}")
    public BaseResponse<AdminCustomerDetail> getById(@PathVariable UUID customerId) {
        AdminCustomerDetail data = service.getCustomerById(customerId);
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @PutMapping("/customers/{customerId}/kyc")
    public BaseResponse<KycApprovalResponse> kyc(
            @PathVariable UUID customerId,
            @Valid @RequestBody KycApprovalRequest req
    ) {
        KycApprovalResponse data = service.approveOrRejectKyc(customerId, req);
        return new BaseResponse<>(data, AppConstants.KYC_UPDATED, AppConstants.SUCCESS_CODE);
    }

    @PutMapping("/{customerId}/block")
    public BaseResponse<AdminCustomerDetail> block(
            @PathVariable UUID customerId,
            @RequestParam String reason
    ) {
        AdminCustomerDetail data = service.blockCustomer(customerId, reason);
        return new BaseResponse<>(data, AppConstants.CUSTOMER_BLOCKED, AppConstants.SUCCESS_CODE);
    }

    @PutMapping("/{customerId}/unblock")
    public BaseResponse<AdminCustomerDetail> unblock(@PathVariable UUID customerId) {
        AdminCustomerDetail data = service.unblockCustomer(customerId);
        return new BaseResponse<>(data, AppConstants.CUSTOMER_UNBLOCKED, AppConstants.SUCCESS_CODE);
    }

    @PutMapping("/{customerId}")
    public BaseResponse<Void> update(
            @PathVariable UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest req
    ) {
        service.updateCustomer(customerId, req);
        return new BaseResponse<>(null, AppConstants.CUSTOMER_UPDATED, AppConstants.SUCCESS_CODE);
    }

    @DeleteMapping("/{customerId}")
    public BaseResponse<Void> delete(@PathVariable UUID customerId) {
        service.deleteCustomer(customerId);
        return new BaseResponse<>(null, AppConstants.CUSTOMER_DELETED, AppConstants.SUCCESS_CODE);
    }
}