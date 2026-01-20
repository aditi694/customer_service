package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.entity.Nominee;
import com.bank.customer_service.repository.NomineeRepository;
import com.bank.customer_service.service.NomineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NomineeServiceImpl implements NomineeService {

    private final NomineeRepository nomineeRepository;

    @Override
    public NomineeDto getByCustomerId(UUID customerId) {

        return nomineeRepository.findByCustomerId(customerId)
                .map(n -> {
                    NomineeDto dto = new NomineeDto();
                    dto.setName(n.getName());
                    dto.setRelation(n.getRelation());
                    dto.setDob(n.getDob());
                    return dto;
                })
                .orElse(null);
    }
}
