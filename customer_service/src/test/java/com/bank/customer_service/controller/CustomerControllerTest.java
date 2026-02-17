package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.service.NomineeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private NomineeService nomineeService;

    @InjectMocks
    private CustomerController controller;

    @Test
    void getNominee_success() {
        UUID customerId = UUID.randomUUID();
        NomineeDto nomineeDto = new NomineeDto();

        when(nomineeService.getByCustomerId(customerId))
                .thenReturn(nomineeDto);

        BaseResponse<NomineeDto> response =
                controller.getNominee(customerId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                response.getResultInfo().getResultMsg());

        verify(nomineeService).getByCustomerId(customerId);
    }
}
