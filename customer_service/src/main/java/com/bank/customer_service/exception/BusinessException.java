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

    public static BusinessException duplicateCustomer(String email) {
        return new BusinessException(
                ErrorCode.DUPLICATE_CUSTOMER,
                "Customer with email '" + email + "' already exists",
                HttpStatus.CONFLICT
        );
    }

    public static BusinessException customerNotFound(Object id) {
        return new BusinessException(
                ErrorCode.CUSTOMER_NOT_FOUND,
                "Customer not found with id: " + id,
                HttpStatus.NOT_FOUND
        );
    }

    public static BusinessException invalidInput(String msg) {
        return new BusinessException(ErrorCode.INVALID_INPUT, msg, HttpStatus.BAD_REQUEST);
    }

}
