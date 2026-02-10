package com.bank.customer_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminCustomerSummary {

    private UUID customerId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String kycStatus;
    private LocalDateTime createdAt;

}
