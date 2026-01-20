package com.bank.customer_service.dto.response;

import com.bank.customer_service.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class KycApprovalResponse {

    private UUID customerId;
    private KycStatus kycStatus;
    private LocalDateTime verifiedAt;
}
