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

    // 400 BAD REQUEST
    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static BusinessException invalidRequest() {
        return badRequest("Invalid request data provided");
    }

    public static BusinessException invalidEmail() {
        return new BusinessException(
                "Invalid email format. Please provide a valid email address",
                HttpStatus.BAD_REQUEST,
                "INVALID_EMAIL"
        );
    }

    public static BusinessException invalidPhone() {
        return new BusinessException(
                "Invalid phone number. Must be 10 digits",
                HttpStatus.BAD_REQUEST,
                "INVALID_PHONE"
        );
    }

    public static BusinessException invalidAadhaar() {
        return new BusinessException(
                "Invalid Aadhaar number. Must be 12 digits",
                HttpStatus.BAD_REQUEST,
                "INVALID_AADHAAR"
        );
    }

    public static BusinessException invalidPan() {
        return new BusinessException(
                "Invalid PAN format. Expected format: ABCDE1234F",
                HttpStatus.BAD_REQUEST,
                "INVALID_PAN"
        );
    }

    public static BusinessException underageCustomer() {
        return new BusinessException(
                "Customer must be at least 18 years old to open an account",
                HttpStatus.BAD_REQUEST,
                "UNDERAGE_CUSTOMER"
        );
    }

    public static BusinessException passwordMismatch() {
        return new BusinessException(
                "Password and confirm password do not match",
                HttpStatus.BAD_REQUEST,
                "PASSWORD_MISMATCH"
        );
    }

    public static BusinessException weakPassword() {
        return new BusinessException(
                "Password must be at least 8 characters long and contain uppercase, lowercase, number and special character",
                HttpStatus.BAD_REQUEST,
                "WEAK_PASSWORD"
        );
    }

    // 401 UNAUTHORIZED
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

    public static BusinessException sessionExpired() {
        return new BusinessException(
                "Your session has expired. Please login again",
                HttpStatus.UNAUTHORIZED,
                "SESSION_EXPIRED"
        );
    }

    // 403 FORBIDDEN
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static BusinessException customerBlocked() {
        return new BusinessException(
                "Your account has been blocked. Please contact customer support",
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

    public static BusinessException kycNotApproved() {
        return new BusinessException(
                "Your KYC is not approved yet. Please wait for verification",
                HttpStatus.FORBIDDEN,
                "KYC_NOT_APPROVED"
        );
    }

    public static BusinessException kycRejected() {
        return new BusinessException(
                "Your KYC has been rejected. Please contact support",
                HttpStatus.FORBIDDEN,
                "KYC_REJECTED"
        );
    }

    public static BusinessException cannotUpdateAfterKyc() {
        return new BusinessException(
                "Email or phone cannot be updated after KYC approval for security reasons",
                HttpStatus.FORBIDDEN,
                "CANNOT_UPDATE_AFTER_KYC"
        );
    }

    public static BusinessException adminAccessRequired() {
        return new BusinessException(
                "This action requires administrator privileges",
                HttpStatus.FORBIDDEN,
                "ADMIN_ACCESS_REQUIRED"
        );
    }

    // 404 NOT FOUND
    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException customerNotFound() {
        return new BusinessException(
                "Customer not found with the provided ID",
                HttpStatus.NOT_FOUND,
                "CUSTOMER_NOT_FOUND"
        );
    }

    public static BusinessException nomineeNotFound() {
        return new BusinessException(
                "Nominee information not found for this customer",
                HttpStatus.NOT_FOUND,
                "NOMINEE_NOT_FOUND"
        );
    }

    public static BusinessException branchNotFound() {
        return new BusinessException(
                "Bank branch not found with the provided IFSC code",
                HttpStatus.NOT_FOUND,
                "BRANCH_NOT_FOUND"
        );
    }

    // 409 CONFLICT
    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException emailExists() {
        return new BusinessException(
                "An account with this email already exists",
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS"
        );
    }

    public static BusinessException phoneExists() {
        return new BusinessException(
                "An account with this phone number already exists",
                HttpStatus.CONFLICT,
                "PHONE_ALREADY_EXISTS"
        );
    }

    public static BusinessException accountNumberExists() {
        return new BusinessException(
                "This account number is already in use",
                HttpStatus.CONFLICT,
                "ACCOUNT_NUMBER_EXISTS"
        );
    }

    public static BusinessException kycAlreadyApproved() {
        return new BusinessException(
                "KYC is already approved for this customer",
                HttpStatus.CONFLICT,
                "KYC_ALREADY_APPROVED"
        );
    }

    public static BusinessException kycAlreadyRejected() {
        return new BusinessException(
                "KYC is already rejected. Please reapply",
                HttpStatus.CONFLICT,
                "KYC_ALREADY_REJECTED"
        );
    }

    public static BusinessException customerAlreadyBlocked() {
        return new BusinessException(
                "Customer is already blocked",
                HttpStatus.CONFLICT,
                "CUSTOMER_ALREADY_BLOCKED"
        );
    }

    public static BusinessException customerNotBlocked() {
        return new BusinessException(
                "Customer is not currently blocked",
                HttpStatus.CONFLICT,
                "CUSTOMER_NOT_BLOCKED"
        );
    }

    // 500 INTERNAL SERVER ERROR
    public static BusinessException internal(String message) {
        return new BusinessException(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR"
        );
    }

    public static BusinessException accountSyncFailed() {
        return new BusinessException(
                "Failed to sync account with account service. Please try again",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ACCOUNT_SYNC_FAILED"
        );
    }

    public static BusinessException externalServiceError(String serviceName) {
        return new BusinessException(
                String.format("%s is temporarily unavailable. Please try again later", serviceName),
                HttpStatus.SERVICE_UNAVAILABLE,
                "EXTERNAL_SERVICE_ERROR"
        );
    }
}