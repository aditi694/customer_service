package com.bank.customer_service.service;

import com.bank.customer_service.dto.client.AdminCustomerSummary;
import com.bank.customer_service.dto.request.CreateCustomerAccountRequest;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.client.AdminCustomerDetail;
import com.bank.customer_service.dto.response.CreateCustomerAccountResponse;
import com.bank.customer_service.dto.response.KycApprovalResponse;

import java.util.List;
import java.util.UUID;
public interface AdminCustomerService {

    CreateCustomerAccountResponse create(CreateCustomerAccountRequest request);

    List<AdminCustomerSummary> getAllCustomers();

    AdminCustomerDetail getCustomerById(UUID customerId);

    KycApprovalResponse approveOrRejectKyc(UUID customerId, KycApprovalRequest request);

    AdminCustomerDetail blockCustomer(UUID customerId, String reason);

    AdminCustomerDetail unblockCustomer(UUID customerId);

    void updateCustomer(UUID customerId, UpdateCustomerRequest request);

    void deleteCustomer(UUID customerId);
}
