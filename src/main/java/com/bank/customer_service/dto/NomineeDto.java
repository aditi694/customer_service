package com.bank.customer_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NomineeDto {
    private String name;
    private String relation;
    private LocalDate dob;
}
