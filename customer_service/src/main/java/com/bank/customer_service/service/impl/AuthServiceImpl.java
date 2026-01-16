package com.bank.customer_service.service.impl;


import com.bank.customer_service.dto.request.RegisterRequest;
import com.bank.customer_service.dto.response.RegisterResponse;

import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.UserEntity;
import com.bank.customer_service.enums.Role;
import com.bank.customer_service.enums.UserStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.UserRepository;
import com.bank.customer_service.service.AuthService;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(CustomerRepository customerRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse registerCustomer(RegisterRequest request) {

        // 1️⃣ Customer must exist
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        BusinessException.customerNotFound(request.getCustomerId()));

        // 2️⃣ Customer already registered?
        if (userRepository.existsByCustomerId(customer.getId())) {
            throw BusinessException.customerAlreadyRegistered();
        }

        // 3️⃣ Username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.duplicateUsername(request.getUsername());
        }

        // 4️⃣ Create user
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCustomerId(customer.getId());


        userRepository.save(user);

        return new RegisterResponse(
                "Customer registered successfully",
                user.getUsername()
        );
    }
}