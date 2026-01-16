package com.bank.customer_service.exception;

import com.bank.customer_service.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    private BusinessException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ---------- FACTORY METHODS ----------

    public static BusinessException customerNotFound(Object id) {
        return new BusinessException(
                ErrorCode.CUSTOMER_NOT_FOUND,
                "Customer not found with id: " + id,
                HttpStatus.NOT_FOUND
        );
    }

    public static BusinessException accessDenied(String message) {
        return new BusinessException(
                ErrorCode.ACCESS_DENIED,
                message,
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException duplicateUsername(String username) {
        return new BusinessException(
                ErrorCode.DUPLICATE_USERNAME,
                "Username already taken: " + username,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException duplicateCustomer(String email) {
        return new BusinessException(
                ErrorCode.DUPLICATE_CUSTOMER,
                "Customer with email '" + email + "' already exists",
                HttpStatus.CONFLICT
        );
    }

    public static BusinessException customerAlreadyRegistered() {
        return new BusinessException(
                ErrorCode.CUSTOMER_ALREADY_REGISTERED,
                "Customer already registered for login",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException validationError(String message) {
        return new BusinessException(
                ErrorCode.VALIDATION_ERROR,
                message,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException internalError() {
        return new BusinessException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
