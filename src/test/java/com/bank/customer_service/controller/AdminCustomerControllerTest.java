package com.bank.customer_service.controller;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.AdminCustomerDetail;
import com.bank.customer_service.dto.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.BaseResponse;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.service.AdminCustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCustomerControllerTest {

    @Mock
    private AdminCustomerService service;

    @InjectMocks
    private AdminCustomerController controller;

    @Test
    void getAll_success() {
        when(service.getAllCustomers())
                .thenReturn(List.of(new AdminCustomerSummary()));

        BaseResponse<List<AdminCustomerSummary>> response =
                controller.getAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                response.getResultInfo().getResultMsg());

        verify(service).getAllCustomers();
    }

    @Test
    void getById_success() {
        UUID id = UUID.randomUUID();

        when(service.getCustomerById(id))
                .thenReturn(new AdminCustomerDetail());

        BaseResponse<AdminCustomerDetail> response =
                controller.getById(id);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                response.getResultInfo().getResultMsg());

        verify(service).getCustomerById(id);
    }

    @Test
    void kyc_success() {
        UUID id = UUID.randomUUID();
        KycApprovalRequest request = new KycApprovalRequest();

        when(service.approveOrRejectKyc(id, request))
                .thenReturn(new KycApprovalResponse());

        BaseResponse<KycApprovalResponse> response =
                controller.kyc(id, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.KYC_UPDATED,
                response.getResultInfo().getResultMsg());

        verify(service).approveOrRejectKyc(id, request);
    }

    @Test
    void block_success() {
        UUID id = UUID.randomUUID();
        String reason = "Violation";

        when(service.blockCustomer(id, reason))
                .thenReturn(new AdminCustomerDetail());

        BaseResponse<AdminCustomerDetail> response =
                controller.block(id, reason);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.CUSTOMER_BLOCKED,
                response.getResultInfo().getResultMsg());

        verify(service).blockCustomer(id, reason);
    }

    @Test
    void unblock_success() {
        UUID id = UUID.randomUUID();

        when(service.unblockCustomer(id))
                .thenReturn(new AdminCustomerDetail());

        BaseResponse<AdminCustomerDetail> response =
                controller.unblock(id);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.CUSTOMER_UNBLOCKED,
                response.getResultInfo().getResultMsg());

        verify(service).unblockCustomer(id);
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = new UpdateCustomerRequest();

        BaseResponse<Void> response =
                controller.update(id, request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.CUSTOMER_UPDATED,
                response.getResultInfo().getResultMsg());

        verify(service).updateCustomer(id, request);
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();

        BaseResponse<Void> response =
                controller.delete(id);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AppConstants.CUSTOMER_DELETED,
                response.getResultInfo().getResultMsg());

        verify(service).deleteCustomer(id);
    }
}
