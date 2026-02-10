package com.bank.customer_service.controller;
import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.AdminCustomerDetail;
import com.bank.customer_service.dto.AdminCustomerSummary;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.security.JwtFilter;
import com.bank.customer_service.security.JwtUtil;
import com.bank.customer_service.service.AdminCustomerService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = AdminCustomerController.class)
class AdminCustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCustomerService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAllCustomers_success() throws Exception{
    when(service.getAllCustomers()).thenReturn(List.of(AdminCustomerSummary.builder().build()));
    mockMvc.perform(get("/api/admin/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.resultInfo.resultMsg")
                    .value(AppConstants.SUCCESS_MSG));
    verify(service).getAllCustomers();
    }

    @Test
    void getAllCustomersByID_success() throws Exception{
        UUID id = UUID.randomUUID();
        when(service.getCustomerById(id)).
                thenReturn(AdminCustomerDetail.builder().build());
        mockMvc.perform(get("/api/admin/customer/{id}",id ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.resultInfo.resultMsg").value(AppConstants.SUCCESS_MSG));
        verify(service).getCustomerById(id);
    }
    @Test
    void kycStatus_success() throws Exception{
        UUID id = UUID.randomUUID();
        String json = """
        {
          "status": "APPROVED",
          "remarks": "Verified"
        }
        """;
        when(service.approveOrRejectKyc(eq(id),any()))
                .thenReturn(new KycApprovalResponse(id, LocalDateTime.now()));
        mockMvc.perform(put("/api/admin/customers/{id}/kyc",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value(AppConstants.KYC_UPDATED));
        verify(service).approveOrRejectKyc(eq(id),any());
    }
    @Test
    void updateKyc_validationFailure() throws Exception {

        UUID id = UUID.randomUUID();

        String invalidJson = "{}";

        mockMvc.perform(put("/api/admin/customers/{id}/kyc", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(service, never())
                .approveOrRejectKyc(any(), any());
    }

    @Test
    void blockCustomer_success() throws Exception{
        UUID id = UUID.randomUUID();
        when(service.blockCustomer(eq(id),eq("fraud"))).
                thenReturn(AdminCustomerDetail.builder().build());
        mockMvc.perform(put("/api/admin/{id}/block",id)
                .param("reason","fraud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value(AppConstants.CUSTOMER_BLOCKED));
        verify(service).blockCustomer(id,"fraud");
    }
    @Test
    void unblockCustomer_success() throws Exception{
        UUID id = UUID.randomUUID();
        when(service.unblockCustomer(id))
                .thenReturn(AdminCustomerDetail.builder().build());
        mockMvc.perform(put("/api/admin/{id}/unblock",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value(AppConstants.CUSTOMER_UNBLOCKED));
        verify(service).unblockCustomer(id);
    }
    @Test
    void updateCustomer_success() throws Exception{
        UUID id = UUID.randomUUID();

        String json = """
        {
          "email": "new@test.com"
        }
        """;
        mockMvc.perform(put("/api/admin/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value(AppConstants.CUSTOMER_UPDATED));
        verify(service).updateCustomer(eq(id),any());
    }
    @Test
    void deleteCustomer_success() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultInfo.resultMsg")
                        .value(AppConstants.CUSTOMER_DELETED));

        verify(service).deleteCustomer(id);
    }

}