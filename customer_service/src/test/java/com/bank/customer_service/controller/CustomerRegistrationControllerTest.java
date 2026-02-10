package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.security.JwtFilter;
import com.bank.customer_service.security.JwtUtil;

import com.bank.customer_service.service.CustomerRegistrationService;
import com.bank.customer_service.service.NomineeService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = CustomerRegistrationController.class)
class CustomerRegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRegistrationService registrationService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void registerCustomer_success() throws Exception{
        String requestJson = """
        {
          "name": "Aditi Goel",
          "email": "aditi@test.com",
          "phone": "9999999999",
          "password": "password123",
          "confirmPassword": "password123",
          "dob": "2000-01-01",
          "gender": "FEMALE",
          "address": "Delhi",
          "aadhaar": "123412341234",
          "pan": "ABCDE1234F",
          "preferredBankName": "HDFC Bank",
          "preferredCity": "Delhi",
          "preferredBranchName": "Connaught Place"
        }
        """;
        CustomerRegistrationResponse response =
                CustomerRegistrationResponse.builder()
                        .customerId("cust-123")
                        .accountNumber("AC123456")
                        .ifscCode("HDFC0DEL01")
                        .build();

        when(registrationService.registerCustomer(any()))
                .thenReturn(response);
        mockMvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.resultInfo.resultMsg").value(AppConstants.REGISTRATION_SUCCESS));
        verify(registrationService,times(1)).registerCustomer(any());
    }
    @Test
    void register_validationFailure() throws Exception {

        String invalidJson = """
        {
          "email": "aditi@test.com"
        }
        """;

        mockMvc.perform(post("/api/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(registrationService, never())
                .registerCustomer(any());
    }
}