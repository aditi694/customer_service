package com.bank.customer_service.exception;

import com.bank.customer_service.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void badRequest_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.badRequest("Invalid input");

        assertEquals("Invalid input", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("BAD_REQUEST", ex.getErrorCode());
    }

    @Test
    void underageCustomer_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.underageCustomer();

        assertEquals("Customer must be at least 18 years old",
                ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("UNDERAGE_CUSTOMER", ex.getErrorCode());
    }

    @Test
    void unauthorized_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.unauthorized(
                        "Invalid username or password");

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("UNAUTHORIZED", ex.getErrorCode());
        assertEquals("Invalid username or password",
                ex.getMessage());
    }

    @Test
    void forbidden_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.forbidden("Access denied");

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals("FORBIDDEN", ex.getErrorCode());
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void customerDeleted_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.customerDeleted();

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals("CUSTOMER_DELETED", ex.getErrorCode());
        assertEquals(
                "This account has been permanently deleted",
                ex.getMessage());
    }

    @Test
    void notFound_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.notFound("Resource missing");

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("NOT_FOUND", ex.getErrorCode());
        assertEquals("Resource missing", ex.getMessage());
    }

    @Test
    void customerNotFound_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.customerNotFound();

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("CUSTOMER_NOT_FOUND", ex.getErrorCode());
        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void conflict_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.conflict("Conflict occurred");

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("CONFLICT", ex.getErrorCode());
        assertEquals("Conflict occurred", ex.getMessage());
    }

    @Test
    void emailExists_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.emailExists();

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("EMAIL_ALREADY_EXISTS", ex.getErrorCode());
        assertEquals("Email already registered",
                ex.getMessage());
    }

    @Test
    void phoneExists_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.phoneExists();

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("PHONE_ALREADY_EXISTS", ex.getErrorCode());
        assertEquals("Phone already registered",
                ex.getMessage());
    }

    @Test
    void internal_exceptionCreatedCorrectly() {

        BusinessException ex =
                BusinessException.internal("Something went wrong");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getStatus());
        assertEquals("INTERNAL_ERROR", ex.getErrorCode());
        assertEquals("Something went wrong", ex.getMessage());
    }
}
