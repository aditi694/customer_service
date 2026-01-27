package com.bank.customer_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRegistrationResponse {

    private boolean success;
    private String message;

    private String customerId;
    private String accountNumber;

    private String ifscCode;
    private String bankName;
    private String branchName;

    private String kycStatus;
    private String accountStatus;

    private String loginUrl;
    private String loginInstructions;
    private String nextSteps;
}