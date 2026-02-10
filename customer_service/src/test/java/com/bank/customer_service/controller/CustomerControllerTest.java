package com.bank.customer_service.controller;
import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.security.JwtFilter;
import com.bank.customer_service.security.JwtUtil;

import com.bank.customer_service.service.NomineeService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = CustomerController.class)
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NomineeService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getNominee_success() throws Exception{
        UUID id = UUID.randomUUID();
        NomineeDto dto = new NomineeDto();
        dto.setName("John");
        dto.setRelation("Father");

        when(service.getByCustomerId(id)).thenReturn(dto);

        mockMvc.perform(get("/customers/{id}/nominee",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.resultInfo.resultMsg").value(AppConstants.SUCCESS_MSG));
        verify(service).getByCustomerId(id);
    }

}