package com.bank.customer_service.dto;

import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminCustomerDetail {
    private UUID customerId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;
    private CustomerStatus status;
    private KycStatus kycStatus;
    private LocalDateTime kycVerifiedAt;
    private LocalDateTime createdAt;
}
