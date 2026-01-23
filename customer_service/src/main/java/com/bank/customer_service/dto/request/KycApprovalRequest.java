package com.bank.customer_service.dto.request;

import com.bank.customer_service.enums.KycMethod;
import com.bank.customer_service.enums.KycStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KycApprovalRequest {
    @NotNull
    private KycStatus status;
    private String remarks;
}
