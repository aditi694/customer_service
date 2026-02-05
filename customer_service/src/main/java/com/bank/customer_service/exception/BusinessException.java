package com.bank.customer_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    private BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static BusinessException underageCustomer() {
        return new BusinessException(
                "Customer must be at least 18 years old",
                HttpStatus.BAD_REQUEST,
                "UNDERAGE_CUSTOMER"
        );
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static BusinessException customerBlocked() {
        return new BusinessException(
                "Your account has been blocked. Please contact support",
                HttpStatus.FORBIDDEN,
                "CUSTOMER_BLOCKED"
        );
    }

    public static BusinessException customerDeleted() {
        return new BusinessException(
                "This account has been permanently deleted",
                HttpStatus.FORBIDDEN,
                "CUSTOMER_DELETED"
        );
    }

    public static BusinessException adminAccessRequired() {
        return new BusinessException(
                "This action requires administrator privileges",
                HttpStatus.FORBIDDEN,
                "ADMIN_ACCESS_REQUIRED"
        );
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException customerNotFound() {
        return new BusinessException(
                "Customer not found",
                HttpStatus.NOT_FOUND,
                "CUSTOMER_NOT_FOUND"
        );
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException emailExists() {
        return new BusinessException(
                "Email already registered",
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS"
        );
    }

    public static BusinessException phoneExists() {
        return new BusinessException(
                "Phone already registered",
                HttpStatus.CONFLICT,
                "PHONE_ALREADY_EXISTS"
        );
    }

    public static BusinessException kycAlreadyApproved() {
        return new BusinessException(
                "KYC is already approved",
                HttpStatus.CONFLICT,
                "KYC_ALREADY_APPROVED"
        );
    }

    public static BusinessException customerAlreadyBlocked() {
        return new BusinessException(
                "Customer is already blocked",
                HttpStatus.CONFLICT,
                "CUSTOMER_ALREADY_BLOCKED"
        );
    }

    // ====================== 500 INTERNAL / 503 SERVICE UNAVAILABLE ======================
    public static BusinessException internal(String message) {
        return new BusinessException(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR"
        );
    }

    public static BusinessException accountSyncFailed() {
        return new BusinessException(
                "Failed to sync account with account service. Please try again",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ACCOUNT_SYNC_FAILED"
        );
    }
}