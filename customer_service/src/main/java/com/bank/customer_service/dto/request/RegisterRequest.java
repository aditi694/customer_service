package com.bank.customer_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    private UUID customerId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
