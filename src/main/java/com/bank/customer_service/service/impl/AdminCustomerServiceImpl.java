package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.client.AdminCustomerDetail;
import com.bank.customer_service.dto.client.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.CustomerAudit;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerAuditRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.AdminCustomerService;
import com.bank.customer_service.validation.CustomerValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerAuditRepository auditRepo;

    // ===================== READ =====================

    @Override
    public List<AdminCustomerSummary> getAllCustomers() {
        return customerRepo.findAll().stream()
                .filter(c -> c.getStatus() != CustomerStatus.DELETED)
                .map(this::mapSummary)
                .toList();
    }

    @Override
    public AdminCustomerDetail getCustomerById(UUID customerId) {
        return mapDetail(getCustomer(customerId));
    }

    // ===================== KYC =====================

    @Override
    public KycApprovalResponse approveOrRejectKyc(
            UUID customerId,
            KycApprovalRequest req
    ) {
        Customer customer = getCustomer(customerId);

        CustomerValidator.validateKyc(customer, req);

        customer.setKycStatus(req.getStatus());
        customer.setKycVerifiedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        audit("KYC_" + req.getStatus(), customerId, req.getRemarks());

        return new KycApprovalResponse(
                customer.getId(),
                customer.getKycVerifiedAt()
        );
    }

    // ===================== BLOCK / UNBLOCK =====================

    @Override
    public AdminCustomerDetail blockCustomer(UUID customerId, String reason) {
        Customer customer = getCustomer(customerId);

        CustomerValidator.validateBlock(customer, reason);

        customer.setStatus(CustomerStatus.BLOCKED);
        customer.setUpdatedAt(LocalDateTime.now());

        audit("CUSTOMER_BLOCKED", customerId, reason);
        return mapDetail(customer);
    }

    @Override
    public AdminCustomerDetail unblockCustomer(UUID customerId) {
        Customer customer = getCustomer(customerId);

        CustomerValidator.validateUnblock(customer);

        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());

        audit("CUSTOMER_UNBLOCKED", customerId, "Unblocked");
        return mapDetail(customer);
    }

    // ===================== UPDATE =====================

    @Override
    public void updateCustomer(UUID customerId, UpdateCustomerRequest req) {
        Customer customer = getCustomer(customerId);

        CustomerValidator.validateUpdate(customer, req);

        if (req.getEmail() != null) {
            customer.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            customer.setPhone(req.getPhone());
        }
        if (req.getAddress() != null) {
            customer.setAddress(req.getAddress());
        }

        customer.setUpdatedAt(LocalDateTime.now());
        audit("CUSTOMER_UPDATED", customerId, "Profile updated");
    }

    // ===================== DELETE =====================

    @Override
    public void deleteCustomer(UUID customerId) {
        Customer customer = getCustomer(customerId);

        CustomerValidator.validateDelete(customer);

        customer.setStatus(CustomerStatus.DELETED);
        customer.setUpdatedAt(LocalDateTime.now());

        audit("CUSTOMER_DELETED", customerId, "Soft delete");
    }

    // ===================== HELPERS =====================

    private Customer getCustomer(UUID id) {
        return customerRepo.findById(id)
                .orElseThrow(BusinessException::customerNotFound);
    }

    private void audit(String action, UUID customerId, String reason) {
        auditRepo.save(CustomerAudit.builder()
                .customerId(customerId)
                .action(action)
                .reason(reason)
                .performedBy("ADMIN")
                .timestamp(LocalDateTime.now())
                .build());
    }

    private AdminCustomerSummary mapSummary(Customer c) {
        return AdminCustomerSummary.builder()
                .customerId(c.getId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .status(c.getStatus().name())
                .kycStatus(c.getKycStatus().name())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private AdminCustomerDetail mapDetail(Customer c) {
        return AdminCustomerDetail.builder()
                .customerId(c.getId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .dob(c.getDob())
                .gender(c.getGender())
                .address(c.getAddress())
                .status(c.getStatus())
                .kycStatus(c.getKycStatus())
                .kycVerifiedAt(c.getKycVerifiedAt())
                .createdAt(c.getCreatedAt())
                .build();
    }
}