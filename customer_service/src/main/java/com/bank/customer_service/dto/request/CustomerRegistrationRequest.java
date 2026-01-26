package com.bank.customer_service.dto.request;

import com.bank.customer_service.dto.NomineeDto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerRegistrationRequest {

    // Personal Details
    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;

    // KYC Documents
    private String aadhaar;
    private String pan;

    // Account Preferences
    private String preferredBankName;
    private String preferredCity;
    private String preferredBranchName;
    private String accountType; // SAVINGS, CURRENT

    // Nominee (Optional)
    private NomineeDto nominee;

    // Password
    private String password;
    private String confirmPassword;
}