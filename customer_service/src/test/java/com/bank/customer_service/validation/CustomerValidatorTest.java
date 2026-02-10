package com.bank.customer_service.validation;

import com.bank.customer_service.dto.request.CreateCustomerAccountRequest;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTest {

    @Mock
    private CustomerRepository repo;

    private CreateCustomerAccountRequest createReq;
    private CustomerRegistrationRequest regReq;

    @BeforeEach
    void setUp() {
        createReq = new CreateCustomerAccountRequest();
        createReq.setEmail("test@mail.com");
        createReq.setPhone("9999999999");

        regReq = new CustomerRegistrationRequest();
        regReq.setEmail("test@mail.com");
        regReq.setPhone("9999999999");
        regReq.setDob(LocalDate.now().minusYears(20));
        regReq.setPassword("pass");
        regReq.setConfirmPassword("pass");
    }

    @Test
    void validateCreate_validRequest() {
        when(repo.existsByEmail(any())).thenReturn(false);
        when(repo.existsByPhone(any())).thenReturn(false);

        assertDoesNotThrow(() ->
                CustomerValidator.validateCreate(createReq, repo));
    }

    @Test
    void validateCreate_nullRequest() {
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> CustomerValidator.validateCreate(null, repo)
        );

        assertEquals("Request is required", ex.getMessage());
    }

    @Test
    void validateCreate_emailExists() {
        when(repo.existsByEmail(any())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateCreate(createReq, repo));
    }

    @Test
    void validateCreate_phoneExists() {
        when(repo.existsByEmail(any())).thenReturn(false);
        when(repo.existsByPhone(any())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateCreate(createReq, repo));
    }

    @Test
    void validateRegistration_valid() {
        when(repo.existsByEmail(any())).thenReturn(false);
        when(repo.existsByPhone(any())).thenReturn(false);

        assertDoesNotThrow(() ->
                CustomerValidator.validateRegistration(regReq, repo));
    }

    @Test
    void validateRegistration_nullRequest() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateRegistration(null, repo));
    }

    @Test
    void validateRegistration_emailExists() {
        when(repo.existsByEmail(any())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateRegistration(regReq, repo));
    }

    @Test
    void validateRegistration_phoneExists() {
        when(repo.existsByEmail(any())).thenReturn(false);
        when(repo.existsByPhone(any())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateRegistration(regReq, repo));
    }
    @Test
    void validateRegistration_underage() {
        regReq.setDob(LocalDate.now().minusYears(16));

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateRegistration(regReq, repo));
    }

    @Test
    void validateRegistration_passwordMismatch() {
        regReq.setConfirmPassword("wrong");

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateRegistration(regReq, repo));
    }

    @Test
    void validateKyc_blockedCustomer() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateKyc(
                        customer(CustomerStatus.BLOCKED, KycStatus.PENDING),
                        kycReq(KycStatus.APPROVED)));
    }

    @Test
    void validateKyc_alreadyApproved() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateKyc(
                        customer(CustomerStatus.ACTIVE, KycStatus.APPROVED),
                        kycReq(KycStatus.APPROVED)));
    }

    @Test
    void validateKyc_alreadyRejected() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateKyc(
                        customer(CustomerStatus.ACTIVE, KycStatus.REJECTED),
                        kycReq(KycStatus.REJECTED)));
    }

    @Test
    void validateKyc_statusNull() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateKyc(
                        customer(CustomerStatus.ACTIVE, KycStatus.PENDING),
                        new KycApprovalRequest()));
    }

    @Test
    void validateKyc_success() {
        assertDoesNotThrow(() ->
                CustomerValidator.validateKyc(
                        customer(CustomerStatus.ACTIVE, KycStatus.PENDING),
                        kycReq(KycStatus.APPROVED)));
    }

    @Test
    void validateUpdate_deletedCustomer() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setEmail("new@test.com");

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateUpdate(
                        customer(CustomerStatus.DELETED, null), req));
    }

    @Test
    void validateUpdate_blockedCustomer() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setEmail("test@mail.com");

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateUpdate(
                        customer(CustomerStatus.BLOCKED, KycStatus.PENDING), req));
    }

    @Test
    void validateUpdate_kycApproved_emailChange() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setEmail("new@mail.com");

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateUpdate(
                        customer(CustomerStatus.ACTIVE, KycStatus.APPROVED), req));
    }

    @Test
    void validateUpdate_success() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setAddress("Delhi");

        assertDoesNotThrow(() ->
                CustomerValidator.validateUpdate(
                        customer(CustomerStatus.ACTIVE, KycStatus.PENDING), req));
    }

    @Test
    void validateUpdate_kycApproved_phoneChange() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setPhone("8888888888");

        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateUpdate(
                        customer(CustomerStatus.ACTIVE, KycStatus.APPROVED), req));
    }

    @Test
    void validateUpdate_kycApproved_noSensitiveChange() {
        UpdateCustomerRequest req = new UpdateCustomerRequest();
        req.setAddress("Delhi");

        assertDoesNotThrow(() ->
                CustomerValidator.validateUpdate(
                        customer(CustomerStatus.ACTIVE, KycStatus.APPROVED), req));
    }

    @Test
    void validateBlock_missingReason() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateBlock(
                        customer(CustomerStatus.ACTIVE, null), ""));
    }

    @Test
    void validateBlock_alreadyBlocked() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateBlock(
                        customer(CustomerStatus.BLOCKED, null), "fraud"));
    }

    @Test
    void validateBlock_success() {
        assertDoesNotThrow(() ->
                CustomerValidator.validateBlock(
                        customer(CustomerStatus.ACTIVE, null), "fraud"));
    }

    @Test
    void validateBlock_reasonNull() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateBlock(
                        customer(CustomerStatus.ACTIVE, null), null));
    }

    @Test
    void validateBlock_validReason() {
        assertDoesNotThrow(() ->
                CustomerValidator.validateBlock(
                        customer(CustomerStatus.ACTIVE, null), "fraud"));
    }

    @Test
    void validateUnblock_notBlocked() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateUnblock(
                        customer(CustomerStatus.ACTIVE, null)));
    }

    @Test
    void validateUnblock_success() {
        assertDoesNotThrow(() ->
                CustomerValidator.validateUnblock(
                        customer(CustomerStatus.BLOCKED, null)));
    }

    @Test
    void validateDelete_alreadyDeleted() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateDelete(
                        customer(CustomerStatus.DELETED, null)));
    }

    @Test
    void validateDelete_blockedCustomer() {
        assertThrows(BusinessException.class,
                () -> CustomerValidator.validateDelete(
                        customer(CustomerStatus.BLOCKED, null)));
    }

    @Test
    void validateDelete_success() {
        assertDoesNotThrow(() ->
                CustomerValidator.validateDelete(
                        customer(CustomerStatus.ACTIVE, null)));
    }
    private Customer customer(CustomerStatus status, KycStatus kyc) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .status(status)
                .kycStatus(kyc)
                .build();
    }

    private KycApprovalRequest kycReq(KycStatus status) {
        KycApprovalRequest req = new KycApprovalRequest();
        req.setStatus(status);
        return req;
    }
}
