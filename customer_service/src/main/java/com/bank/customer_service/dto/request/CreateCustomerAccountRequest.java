package com.bank.customer_service.dto.request;

import com.bank.customer_service.dto.NomineeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCustomerAccountRequest {

    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;

    private String aadhaar;
    private String pan;
    private String bankName;
    private String city;
    private String branchName;

    private String accountType;
    private NomineeDto nominee;
}
