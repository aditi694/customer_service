package com.bank.customer_service.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthUser {

    private String username;
    private String role;
    private UUID customerId; // null for admin/staff
}

