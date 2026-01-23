package com.bank.customer_service.controller;

import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.service.NomineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final NomineeService nomineeService;

    @GetMapping("/{customerId}/nominee")
    public NomineeDto getNominee(@PathVariable UUID customerId) {
        return nomineeService.getByCustomerId(customerId);
    }

}
