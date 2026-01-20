package com.bank.customer_service.dto.response;

import com.bank.customer_service.enums.KycStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCustomerAccountResponse {

    private boolean success;
    private String customerId;
    private String accountNumber;
    private String password;
    private String kycStatus;
}
