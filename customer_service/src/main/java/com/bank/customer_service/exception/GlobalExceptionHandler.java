package com.bank.customer_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        log.error("Business exception: {} - {}", ex.getStatus(), ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        ex.getMessage(),
                        ex.getStatus().value(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected exception", ex);

        return ResponseEntity
                .status(500)
                .body(new ApiErrorResponse(
                        "An unexpected error occurred. Please try again later.",
                        500,
                        LocalDateTime.now()
                ));
    }
}