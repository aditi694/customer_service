package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.mapper.CustomerMapper;
import com.bank.customer_service.dto.request.CustomerCreateRequest;
import com.bank.customer_service.dto.request.KycVerifyRequest;
import com.bank.customer_service.dto.response.ApiSuccessResponse;
import com.bank.customer_service.dto.response.CustomerResponse;
import com.bank.customer_service.security.AuthUser;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerBlockReason;
import com.bank.customer_service.enums.CustomerCloseReason;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerAuditRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerService;
import com.bank.customer_service.util.CustomerAuditUtil;
import com.bank.customer_service.util.MaskingUtil;
import com.bank.customer_service.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerAuditRepository auditRepo;

    public CustomerServiceImpl(CustomerRepository customerRepo,
                               CustomerAuditRepository auditRepo) {
        this.customerRepo = customerRepo;
        this.auditRepo = auditRepo;
    }

    @Override
    public CustomerResponse create(CustomerCreateRequest request) {

        if (customerRepo.existsByEmail(request.getEmail())) {
            throw BusinessException.duplicateCustomer(request.getEmail());
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .panMasked(MaskingUtil.maskPan(request.getPanNumber()))
                .aadhaarMasked(MaskingUtil.maskAadhaar(request.getAadhaarNumber()))
                .kycStatus(KycStatus.PENDING)
                .status(CustomerStatus.ACTIVE)
                .build();

        Customer saved = customerRepo.save(customer);
        CustomerAuditUtil.logCreate(auditRepo, saved.getId());

        return CustomerMapper.toResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public CustomerResponse getById(UUID id) {

        AuthUser user = SecurityUtil.getCurrentUser();

        // 👤 CUSTOMER → ONLY OWN DATA
        if (user != null && "ROLE_CUSTOMER".equals(user.getRole())) {
            if (!id.equals(user.getCustomerId())) {
                throw BusinessException.accessDenied(
                        "You are not allowed to access other customer data"
                );
            }
        }

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> BusinessException.customerNotFound(id));

        return CustomerMapper.toResponse(customer);
    }


    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<CustomerResponse> getCustomers(CustomerStatus status, Pageable pageable) {

        if (status == null) {
            return customerRepo.findAll(pageable)
                    .map(CustomerMapper::toResponse);
        }

        return customerRepo.findByStatus(status, pageable)
                .map(CustomerMapper::toResponse);
    }

    @Override
    public ApiSuccessResponse blockCustomer(UUID id, CustomerBlockReason reason) {
        return updateStatus(id, CustomerStatus.BLOCKED, reason.name());
    }

    @Override
    public ApiSuccessResponse closeCustomer(UUID id, CustomerCloseReason reason) {
        return updateStatus(id, CustomerStatus.CLOSED, reason.name());
    }

    private ApiSuccessResponse updateStatus(
            UUID id,
            CustomerStatus newStatus,
            String reason
    ) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> BusinessException.customerNotFound(id));

        CustomerStatus oldStatus = customer.getStatus();
        customer.changeStatus(newStatus);

        AuthUser actor = SecurityUtil.getCurrentUser();

        CustomerAuditUtil.logStatusChange(
                auditRepo,
                id,
                oldStatus.name(),
                newStatus.name(),
                actor != null ? actor.getUsername() : "SYSTEM",
                actor != null ? actor.getRole() : "SYSTEM",
                reason
        );

        return ApiSuccessResponse.success(
                "Customer status updated successfully",
                id,
                oldStatus.name(),
                newStatus.name(),
                reason
        );
    }

    @Override
    public ApiSuccessResponse verifyKyc(UUID customerId, KycVerifyRequest request) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> BusinessException.customerNotFound(customerId));

        customer.verifyKyc(
                request.getStatus(),
                request.getMethod()
        );

        CustomerAuditUtil.logKycVerification(
                auditRepo,
                customerId,
                request.getStatus(),
                request.getMethod(),
                request.getRemarks(),
                "COMPLIANCE_OFFICER"
        );

        return ApiSuccessResponse.builder()
                .message("KYC verification recorded successfully")
                .customerId(customerId)
                .oldStatus("PENDING")
                .newStatus(request.getStatus().name())
                .reason(request.getMethod().name())
                .timestamp(LocalDateTime.now())
                .build();
    }

}
