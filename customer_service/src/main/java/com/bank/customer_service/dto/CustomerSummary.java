package com.bank.customer_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerSummary {

    private UUID customerId;
    private String fullName;
    private String kycStatus;
    private String nomineeName;
    private String nomineeRelation;
}
