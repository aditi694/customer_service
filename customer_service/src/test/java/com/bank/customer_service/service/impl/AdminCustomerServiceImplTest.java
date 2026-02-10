package com.bank.customer_service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.CustomerAuditRepository;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.dto.AdminCustomerDetail;
import com.bank.customer_service.dto.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.KycApprovalResponse;

import com.bank.customer_service.validation.CustomerValidator;
import com.bank.customer_service.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AdminCustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private CustomerAuditRepository auditRepo;

    @InjectMocks
    private AdminCustomerServiceImpl service;

    private Customer activeCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();

        activeCustomer = Customer.builder()
                .id(customerId)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .phone("9999999999")
                .address("Delhi")
                .status(CustomerStatus.ACTIVE)
                .kycStatus(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
    @Test
    void getAllCustomers_allActive() {

        when(customerRepo.findAll())
                .thenReturn(List.of(activeCustomer));

        List<AdminCustomerSummary> result =
                service.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());

        verify(customerRepo).findAll();
    }
    @Test
    void getAllCustomers_filtersDeleted() {

        Customer deleted = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Deleted User")
                .email("deleted@test.com")
                .phone("1111111111")
                .status(CustomerStatus.DELETED)
                .kycStatus(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepo.findAll())
                .thenReturn(List.of(activeCustomer, deleted));

        List<AdminCustomerSummary> result =
                service.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals(CustomerStatus.ACTIVE.name(),
                result.get(0).getStatus());
    }
    @Test
    void getAllCustomers_emptyList() {

        when(customerRepo.findAll())
                .thenReturn(List.of());

        List<AdminCustomerSummary> result =
                service.getAllCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void getCustomerById_success() {

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        AdminCustomerDetail detail =
                service.getCustomerById(customerId);

        assertNotNull(detail);
        assertEquals(customerId, detail.getCustomerId());
    }
    @Test
    void getCustomerById_notFound() {

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.getCustomerById(customerId));
    }
    @Test
    void approveKyc_success() {

        KycApprovalRequest req = new KycApprovalRequest();
        req.setStatus(KycStatus.APPROVED);
        req.setRemarks("Verified");

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateKyc(any(), any()))
                    .thenAnswer(inv -> null);

            KycApprovalResponse response =
                    service.approveOrRejectKyc(customerId, req);

            assertEquals(KycStatus.APPROVED,
                    activeCustomer.getKycStatus());

            assertNotNull(response.getVerifiedAt());

            verify(auditRepo).save(any());
        }
    }
    @Test
    void approveKyc_validationFails() {

        KycApprovalRequest req = new KycApprovalRequest();
        req.setStatus(KycStatus.APPROVED);
        req.setRemarks("Bad");


        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateKyc(any(), any()))
                    .thenThrow(BusinessException.badRequest("Invalid"));

            assertThrows(BusinessException.class,
                    () -> service.approveOrRejectKyc(customerId, req));

            verify(auditRepo, never()).save(any());
        }
    }
    @Test
    void approveKyc_statusAndRemarksNull() {

        KycApprovalRequest req = new KycApprovalRequest();
        req.setStatus(null);
        req.setRemarks(null);

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateKyc(any(), any()))
                    .thenAnswer(inv -> null);

            KycApprovalResponse response =
                    service.approveOrRejectKyc(customerId, req);

            assertNotNull(response.getVerifiedAt());
            verify(auditRepo).save(any());
        }
    }

    @Test
    void blockCustomer_success() {

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateBlock(any(), any()))
                    .thenAnswer(inv -> null);

            service.blockCustomer(customerId, "Fraud");

            assertEquals(CustomerStatus.BLOCKED,
                    activeCustomer.getStatus());

            verify(auditRepo).save(any());
        }
    }

    @Test
    void unblockCustomer_success() {

        activeCustomer.setStatus(CustomerStatus.BLOCKED);

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateUnblock(any()))
                    .thenAnswer(inv -> null);

            service.unblockCustomer(customerId);

            assertEquals(CustomerStatus.ACTIVE,
                    activeCustomer.getStatus());

            verify(auditRepo).save(any());
        }
    }
    @Test
    void updateCustomer_success() {

        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setEmail("new@test.com");
        req.setPhone("8888888888");


        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateUpdate(any(), any()))
                    .thenAnswer(inv -> null);

            service.updateCustomer(customerId, req);

            assertEquals("new@test.com", activeCustomer.getEmail());
            assertEquals("8888888888", activeCustomer.getPhone());

            verify(auditRepo).save(any());
        }
    }
    @Test
    void updateCustomer_onlyAddress() {

        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setAddress("Mumbai");

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateUpdate(any(), any()))
                    .thenAnswer(inv -> null);

            service.updateCustomer(customerId, req);

            assertEquals("Mumbai", activeCustomer.getAddress());
            verify(auditRepo).save(any());
        }
    }


    @Test
    void deleteCustomer_success() {

        when(customerRepo.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateDelete(any()))
                    .thenAnswer(inv -> null);

            service.deleteCustomer(customerId);

            assertEquals(CustomerStatus.DELETED,
                    activeCustomer.getStatus());

            verify(auditRepo).save(any());
        }
    }
}
