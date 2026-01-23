package com.bank.customer_service.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    private BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST);
    }
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED);
    }

    public static BusinessException unauthorized() {
        return unauthorized("Unauthorized access");
    }


    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN);
    }

    public static BusinessException internal(String message) {
        return new BusinessException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static BusinessException customerNotFound() {
        return notFound("Customer not found");
    }

    public static BusinessException emailExists() {
        return conflict("Email already exists");
    }

    public static BusinessException phoneExists() {
        return conflict("Phone already exists");
    }

    public static BusinessException customerDeleted() {
        return conflict("Customer is deleted");
    }

    public static BusinessException invalidRequest() {
        return badRequest("Invalid request data");
    }
}
