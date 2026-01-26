package com.bank.customer_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRegistrationResponse {

    private boolean success;
    private String message;

    // Customer & Account Details
    private String customerId;
    private String accountNumber;

    // Bank Details
    private String ifscCode;
    private String bankName;
    private String branchName;

    // Status Information
    private String kycStatus;
    private String accountStatus;

    // Next Steps
    private String loginUrl;
    private String loginInstructions;
    private String nextSteps;
}