package com.bank.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailResponse {
    private String customerId;
    private String fullName;
    private String email;
    private String phone;
    private String kycStatus;
    private String nomineeName;
    private String nomineeRelation;
}