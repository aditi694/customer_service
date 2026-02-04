package com.bank.customer_service.dto.request;

import com.bank.customer_service.dto.NomineeDto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerRegistrationRequest {

    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;
    private String aadhaar;
    private String pan;
    private String preferredBankName;
    private String preferredCity;
    private String preferredBranchName;
    private String accountType;
    private NomineeDto nominee;
    private String password;
    private String confirmPassword;
}