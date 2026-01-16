package com.bank.customer_service.util;

import com.bank.customer_service.security.AuthUser;
import com.bank.customer_service.entity.CustomerAudit;
import com.bank.customer_service.enums.KycMethod;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.repository.CustomerAuditRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CustomerAuditUtil {

    private CustomerAuditUtil() {}

    public static void logCreate(
            CustomerAuditRepository repo,
            UUID customerId
    ) {
        repo.save(CustomerAudit.builder()
                .action("CUSTOMER_CREATED")
                .customerId(customerId)
                .newStatus("ACTIVE")
                .performedBy("SYSTEM")
                .performedByRole("SYSTEM")   // ✅ FIX
                .reason("Customer created")
                .timestamp(LocalDateTime.now())
                .build());
    }


    public static void logStatusChange(
            CustomerAuditRepository repo,
            UUID customerId,
            String oldStatus,
            String newStatus,
            String performedBy,
            String performedByRole,
            String reason
    ) {
        repo.save(CustomerAudit.builder()
                .action("CUSTOMER_STATUS_CHANGED")
                .customerId(customerId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .performedBy(performedBy)
                .performedByRole(performedByRole)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build());
    }


    public static void logKycVerification(
            CustomerAuditRepository repo,
            UUID customerId,
            KycStatus status,
            KycMethod method,
            String remarks,
            String performedBy
    ) {
        repo.save(
                CustomerAudit.builder()
                        .customerId(customerId)
                        .action("KYC_" + status.name())
                        .oldStatus("PENDING")
                        .newStatus(status.name())
                        .performedBy(performedBy)
                        .performedByRole("HUMAN")
                        .reason(method.name() + (remarks != null ? " - " + remarks : ""))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

}
