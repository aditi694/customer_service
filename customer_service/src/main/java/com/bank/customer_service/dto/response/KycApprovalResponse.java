package com.bank.customer_service.dto.response;

import com.bank.customer_service.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycApprovalResponse {

    private UUID customerId;
    private LocalDateTime verifiedAt;
}
