package com.bank.customer_service.dto.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDetailResponse {
    private String customerId;
    private String fullName;
    private String email;
    private String phone;
    private String kycStatus;
    private String nomineeName;
    private String nomineeRelation;
}