package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.entity.Nominee;
import com.bank.customer_service.repository.NomineeRepository;
import com.bank.customer_service.service.impl.NomineeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NomineeServiceImplTest {

    @Mock
    private NomineeRepository nomineeRepository;

    @InjectMocks
    private NomineeServiceImpl service;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }
    @Test
    void getNominee_success() {

        Nominee nominee = Nominee.builder()
                .customerId(customerId)
                .name("Father")
                .relation("FATHER")
                .dob(LocalDate.of(1970, 1, 1))
                .build();

        when(nomineeRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(nominee));

        NomineeDto dto =
                service.getByCustomerId(customerId);

        assertNotNull(dto);
        assertEquals("Father", dto.getName());
        assertEquals("FATHER", dto.getRelation());
        assertEquals(LocalDate.of(1970, 1, 1), dto.getDob());

        verify(nomineeRepository, times(1))
                .findByCustomerId(customerId);
    }
    @Test
    void getNominee_notFound() {

        when(nomineeRepository.findByCustomerId(customerId))
                .thenReturn(Optional.empty());

        NomineeDto dto =
                service.getByCustomerId(customerId);

        assertNull(dto);

        verify(nomineeRepository, times(1))
                .findByCustomerId(customerId);
    }

}
