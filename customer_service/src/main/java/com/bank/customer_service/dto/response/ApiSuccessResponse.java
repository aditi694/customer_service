package com.bank.customer_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApiSuccessResponse {

    private String message;
    private UUID customerId;
    private String oldStatus;
    private String newStatus;
    private String reason;
    private LocalDateTime timestamp;

    // ✅ SAFE STATIC FACTORY
    public static ApiSuccessResponse success(
            String message,
            UUID customerId,
            String oldStatus,
            String newStatus,
            String reason
    ) {
        return ApiSuccessResponse.builder()
                .message(message)
                .customerId(customerId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
