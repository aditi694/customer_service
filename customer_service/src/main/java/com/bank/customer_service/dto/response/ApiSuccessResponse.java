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
}
