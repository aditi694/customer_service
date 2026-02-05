package com.bank.customer_service.exception;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.response.BaseResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Business Exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusiness(BusinessException ex) {
        log.error("Business exception: {} - {}", ex.getStatus(), ex.getMessage());
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                ex.getMessage(),
                ex.getErrorCode() != null ? ex.getErrorCode() : String.valueOf(ex.getStatus().value())
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        String msg = errors.isEmpty() ? "Invalid request data" : errors.toString();
        BaseResponse<Void> response = new BaseResponse<>(null, msg, AppConstants.BAD_REQUEST_CODE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Constraint violations (path/query params)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .findFirst().orElse("Invalid input");
        BaseResponse<Void> response = new BaseResponse<>(null, msg, AppConstants.BAD_REQUEST_CODE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 404 - Wrong URL
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoHandler(NoHandlerFoundException ex) {
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                AppConstants.NOT_FOUND_MSG,
                AppConstants.NOT_FOUND_CODE
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Feign errors
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BaseResponse<Void>> handleFeign(FeignException ex) {
        log.error("Feign error: {}", ex.getMessage());
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                AppConstants.SERVICE_UNAVAILABLE_MSG,
                AppConstants.SERVICE_UNAVAILABLE_CODE
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // DB constraint errors (e.g., duplicate entry)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDBError(DataIntegrityViolationException ex) {
        log.error("DB constraint error", ex);
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                "Database error: Duplicate or invalid data",
                AppConstants.CONFLICT_CODE
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Invalid JSON / Enum
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidJson(HttpMessageNotReadableException ex) {
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                "Invalid JSON or enum value provided",
                AppConstants.BAD_REQUEST_CODE
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        BaseResponse<Void> response = new BaseResponse<>(
                null,
                AppConstants.INTERNAL_ERROR_MSG,
                AppConstants.INTERNAL_ERROR_CODE
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}