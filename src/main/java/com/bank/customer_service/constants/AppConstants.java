package com.bank.customer_service.constants;

public final class AppConstants {
    private AppConstants() {
    }

    public static final String REGISTRATION_SUCCESS = "Registration successful! Your account has been created.";
    public static final String KYC_UPDATED = "KYC status updated successfully";
    public static final String CUSTOMER_BLOCKED = "Customer blocked successfully";
    public static final String CUSTOMER_UNBLOCKED = "Customer unblocked successfully";
    public static final String CUSTOMER_UPDATED = "Customer details updated successfully";
    public static final String CUSTOMER_DELETED = "Customer account deleted successfully";
    // Success
    public static final String SUCCESS_CODE = "00";
    public static final String SUCCESS_MSG = "Operation successful";

    // Errors
    public static final String BAD_REQUEST_CODE = "400";
    public static final String BAD_REQUEST_MSG = "Bad Request";
    public static final String NOT_FOUND_CODE = "404";
    public static final String NOT_FOUND_MSG = "Resource not found";

    public static final String CONFLICT_CODE = "409";

    public static final String INTERNAL_ERROR_CODE = "500";
    public static final String INTERNAL_ERROR_MSG = "Internal server error";

    public static final String SERVICE_UNAVAILABLE_CODE = "503";
    public static final String SERVICE_UNAVAILABLE_MSG = "Service unavailable";


    public static final String DATA_RETRIEVED = "Data retrieved successfully";

}