package com.bank.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSummary {

    private UUID customerId;
    private String fullName;
    private String kycStatus;
    private String nomineeName;
    private String nomineeRelation;
}
