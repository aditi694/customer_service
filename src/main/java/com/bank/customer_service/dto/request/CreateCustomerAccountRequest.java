package com.bank.customer_service.dto.request;

import com.bank.customer_service.dto.NomineeDto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCustomerAccountRequest {
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

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    private String accountType;  // Optional

    private NomineeDto nominee;  // Optional
}