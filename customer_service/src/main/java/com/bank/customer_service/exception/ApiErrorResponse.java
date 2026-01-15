package com.bank.customer_service.exception;

import com.bank.customer_service.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {

    private ErrorCode errorCode;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
