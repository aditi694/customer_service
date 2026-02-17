package com.bank.customer_service.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AdminLoginResponse {
    private String token;
}
