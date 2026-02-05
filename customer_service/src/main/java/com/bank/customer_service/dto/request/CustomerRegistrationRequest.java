package com.bank.customer_service.dto.request;

import com.bank.customer_service.dto.NomineeDto;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRegistrationRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message = "Date of birth is required")
    @Past(message = "DOB must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Aadhaar is required")
    private String aadhaar;

    @NotBlank(message = "PAN is required")
    private String pan;

    @NotBlank(message = "Preferred bank name is required")
    private String preferredBankName;

    @NotBlank(message = "Preferred city is required")
    private String preferredCity;

    @NotBlank(message = "Preferred branch name is required")
    private String preferredBranchName;

    private String accountType;

    private NomineeDto nominee;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}