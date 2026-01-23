package com.bank.customer_service.validation;

import com.bank.customer_service.dto.request.CreateCustomerAccountRequest;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public final class CustomerValidator {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerValidator.class);

    private CustomerValidator() {}


    public static void validateCreate(
            CreateCustomerAccountRequest req,
            CustomerRepository repo
    ) {

        log.info("Customer create validation started");

        if (req == null) {
            log.warn("Create customer failed: request is null");
            throw BusinessException.invalidRequest();
        }

        if (req.getName() == null || req.getName().isBlank()) {
            log.warn("Create customer failed: name missing");
            throw BusinessException.badRequest("Customer name is required");
        }

        if (req.getEmail() == null || req.getEmail().isBlank()) {
            log.warn("Create customer failed: email missing");
            throw BusinessException.badRequest("Email is required");
        }

        if (!req.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("Create customer failed: invalid email format [{}]",
                    req.getEmail());
            throw BusinessException.badRequest("Invalid email format");
        }

        if (repo.existsByEmail(req.getEmail())) {
            log.warn("Create customer failed: email already exists [{}]",
                    req.getEmail());
            throw BusinessException.emailExists();
        }

        if (req.getPhone() == null || !req.getPhone().matches("\\d{10}")) {
            log.warn("Create customer failed: invalid phone number");
            throw BusinessException.badRequest(
                    "Phone number must be 10 digits"
            );
        }

        if (repo.existsByPhone(req.getPhone())) {
            log.warn("Create customer failed: phone already exists [{}]",
                    maskPhone(req.getPhone()));
            throw BusinessException.phoneExists();
        }

        if (req.getDob() == null) {
            log.warn("Create customer failed: DOB missing");
            throw BusinessException.badRequest("Date of birth is required");
        }

        if (req.getDob().plusYears(18).isAfter(LocalDate.now())) {
            log.warn("Create customer failed: underage applicant");
            throw BusinessException.badRequest(
                    "Customer must be at least 18 years old"
            );
        }

        log.info("Customer create validation passed");
    }


    public static void validateKyc(Customer customer, KycApprovalRequest req) {

        log.info("KYC validation started for customerId={}",
                customer.getId());

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            log.warn("KYC validation failed: customer blocked [{}]",
                    customer.getId());
            throw BusinessException.forbidden(
                    "Blocked customer KYC cannot be updated"
            );
        }

        if (customer.getKycStatus() == KycStatus.APPROVED) {
            log.warn("KYC already approved for customerId={}",
                    customer.getId());
            throw BusinessException.conflict("KYC already approved");
        }

        if (customer.getKycStatus() == KycStatus.REJECTED) {
            log.warn("KYC already rejected for customerId={}",
                    customer.getId());
            throw BusinessException.conflict("KYC already rejected");
        }

        if (req.getStatus() == null) {
            log.warn("KYC validation failed: status missing");
            throw BusinessException.badRequest("KYC status is required");
        }

        log.info("KYC validation passed for customerId={}",
                customer.getId());
    }

    public static void validateUpdate(
            Customer customer,
            UpdateCustomerRequest req
    ) {

        log.info("Update validation started for customerId={}",
                customer.getId());

        if (customer.getStatus() == CustomerStatus.DELETED) {
            log.warn("Update failed: customer deleted [{}]",
                    customer.getId());
            throw BusinessException.conflict(
                    "Deleted customer cannot be updated"
            );
        }

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            log.warn("Update failed: customer blocked [{}]",
                    customer.getId());
            throw BusinessException.forbidden(
                    "Blocked customer cannot be updated"
            );
        }

        if (customer.getKycStatus() == KycStatus.APPROVED &&
                (req.getEmail() != null || req.getPhone() != null)) {
            log.warn(
                    "Update failed: email/phone update after KYC for customerId={}",
                    customer.getId()
            );
            throw BusinessException.badRequest(
                    "Email or phone cannot be updated after KYC approval"
            );
        }

        log.info("Update validation passed for customerId={}",
                customer.getId());
    }

    public static void validateBlock(Customer customer, String reason) {

        log.info("Block validation started for customerId={}",
                customer.getId());

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            log.warn("Block failed: customer already blocked [{}]",
                    customer.getId());
            throw BusinessException.conflict("Customer already blocked");
        }

        if (reason == null || reason.isBlank()) {
            log.warn("Block failed: reason missing for customerId={}",
                    customer.getId());
            throw BusinessException.badRequest("Block reason is required");
        }

        log.info("Block validation passed for customerId={}",
                customer.getId());
    }


    public static void validateUnblock(Customer customer) {

        log.info("Unblock validation started for customerId={}",
                customer.getId());

        if (customer.getStatus() != CustomerStatus.BLOCKED) {
            log.warn("Unblock failed: customer is not blocked [{}]",
                    customer.getId());
            throw BusinessException.badRequest("Customer is not blocked");
        }

        log.info("Unblock validation passed for customerId={}",
                customer.getId());
    }



    public static void validateDelete(Customer customer) {

        log.info("Delete validation started for customerId={}",
                customer.getId());

        if (customer.getStatus() == CustomerStatus.DELETED) {
            log.warn("Delete failed: already deleted [{}]",
                    customer.getId());
            throw BusinessException.customerDeleted();
        }

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            log.warn("Delete failed: customer blocked [{}]",
                    customer.getId());
            throw BusinessException.forbidden(
                    "Blocked customer cannot be deleted"
            );
        }

        log.info("Delete validation passed for customerId={}",
                customer.getId());
    }


    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
