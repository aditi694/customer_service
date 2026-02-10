package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CustomerDetailResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.GlobalExceptionHandler;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.security.JwtFilter;
import com.bank.customer_service.security.JwtUtil;

import com.bank.customer_service.service.InternalCustomerService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static com.bank.customer_service.enums.KycStatus.APPROVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = InternalCustomerController.class)
@Import(GlobalExceptionHandler.class)
class InternalCustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InternalCustomerService service;
    @MockBean
    private CustomerRepository custRepo;
    @MockBean
    private BankBranchRepository bankRepo;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void getCustomerDetailById_success() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerDetailResponse response = CustomerDetailResponse.builder()
                .customerId(String.valueOf(id))
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .phone("1234567899")
                .build();
        when(service.getCustomerDetails(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/internal/customers/{id}/detail", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Aditi Goel"));
        verify(service).getCustomerDetails(id);
    }

    @Test
    void getCustomerSummaryById_success() throws Exception {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .fullName("Aditi Goel")
                .kycStatus(APPROVED)
                .nomineeName("Father")
                .nomineeRelation("FATHER")
                .build();

        when(custRepo.findById(customerId))
                .thenReturn(Optional.of(customer));
        mockMvc.perform(get("/api/internal/customers/{id}/summary", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.fullName").value("Aditi Goel"))
                .andExpect(jsonPath("$.kycStatus").value("APPROVED"))
                .andExpect(jsonPath("$.nomineeName")
                        .value("Father"));
        verify(custRepo).findById(customerId);
    }

    @Test
    void getCustomerContactById_success() throws Exception{
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .phone("1234567899")
                .build();
        when(custRepo.findById(customerId))
                .thenReturn(Optional.of(customer));
        mockMvc.perform(get("/api/internal/customers/{id}/contact",customerId))
                .andExpect(status().isOk())
                .andExpect(content().string("1234567899"));
        verify(custRepo).findById(customerId);
    }
    @Test
    void getIfscByAccountNumber_success() throws Exception{
        String accNumber = "AC123";
        Customer customer = Customer.builder()
                .accountNumber(accNumber)
                .ifscCode("HDFC0DEL01")
                .build();
        when(custRepo.findByAccountNumber(accNumber))
                .thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/internal/customers/account/{acc}/ifsc", accNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("HDFC0DEL01"));

        verify(custRepo).findByAccountNumber(accNumber);
    }
    @Test
    void getIfscByAccountNumber_customerNotFound() throws Exception {
        String accNumber = "AC999";

        when(custRepo.findByAccountNumber(accNumber))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/internal/customers/account/{acc}/ifsc", accNumber))
                .andExpect(status().isNotFound())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value("Customer not found for account"));

        verify(custRepo).findByAccountNumber(accNumber);
    }


    @Test
    void getBankBranch_success() throws Exception {

        BankBranch branch = BankBranch.builder()
                .ifscCode("HDFC0DEL01")
                .bankName("HDFC Bank")
                .branchName("Connaught Place")
                .city("Delhi")
                .build();

        when(bankRepo.findByIfscCode("HDFC0DEL01"))
                .thenReturn(Optional.of(branch));

        mockMvc.perform(get("/api/internal/customers/bank-branch/{ifsc}", "HDFC0DEL01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ifscCode").value("HDFC0DEL01"))
                .andExpect(jsonPath("$.bankName").value("HDFC Bank"))
                .andExpect(jsonPath("$.branchName").value("Connaught Place"))
                .andExpect(jsonPath("$.city").value("Delhi"))
                .andExpect(jsonPath("$.address")
                        .value("Connaught Place, Delhi"));

        verify(bankRepo).findByIfscCode("HDFC0DEL01");
    }
    @Test
    void getBankBranch_notFound() throws Exception {

        when(bankRepo.findByIfscCode("HDFC0XXX01"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/internal/customers/bank-branch/{ifsc}", "HDFC0XXX01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value("Bank branch not found"));


        verify(bankRepo).findByIfscCode("HDFC0XXX01");
    }

}