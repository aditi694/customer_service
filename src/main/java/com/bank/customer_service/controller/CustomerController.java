package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.dto.response.BaseResponse;
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
    public BaseResponse<NomineeDto> getNominee(@PathVariable UUID customerId) {
        NomineeDto data = nomineeService.getByCustomerId(customerId);
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }
}