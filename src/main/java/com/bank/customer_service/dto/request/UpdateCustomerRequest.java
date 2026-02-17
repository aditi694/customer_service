package com.bank.customer_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    private String address;
}