package com.bank.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCustomerSummary {

    private UUID customerId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String kycStatus;
    private LocalDateTime createdAt;

}
