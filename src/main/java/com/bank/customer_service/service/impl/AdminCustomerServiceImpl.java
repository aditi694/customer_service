package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.client.AccountClient;
import com.bank.customer_service.dto.AdminCustomerDetail;
import com.bank.customer_service.dto.AdminCustomerSummary;
import com.bank.customer_service.dto.request.KycApprovalRequest;
import com.bank.customer_service.dto.request.UpdateCustomerRequest;
import com.bank.customer_service.dto.response.KycApprovalResponse;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.CustomerAudit;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerAuditRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.AdminCustomerService;
import com.bank.customer_service.validation.CustomerValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bank.customer_service.repository.NomineeRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerAuditRepository auditRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccountClient accountClient;
    private final NomineeRepository nomineeRepository;
    private final BankBranchRepository bankBranchRepo;

    private static final String ACCOUNT_SERVICE_URL =
            "http://localhost:8082/api/internal/accounts/create";


//    @Override
//    public CreateCustomerAccountResponse create(CreateCustomerAccountRequest req) {
//
//        CustomerValidator.validateCreate(req, customerRepo);
//
//        BankBranch branch = bankBranchRepo
//                .findByBankNameAndCityAndBranchName(
//                        req.getBankName(),
//                        req.getCity(),
//                        req.getBranchName()
//                )
//                .orElseGet(() -> {
//                    String ifsc = generateIfsc(
//                            req.getBankName(),
//                            req.getCity()
//                    );
//
//                    return bankBranchRepo.save(
//                            BankBranch.builder()
//                                    .bankName(req.getBankName())
//                                    .city(req.getCity())
//                                    .branchName(req.getBranchName())
//                                    .ifscCode(ifsc)
//                                    .address(req.getAddress())
//                                    .build()
//                    );
//                });
//
//        String accountNumber = generateAccountNumber();
//        String tempPassword = generateTempPassword();
//        String passwordHash = passwordEncoder.encode(tempPassword);
//
//        Customer customer = Customer.builder()
//                .fullName(req.getName())
//                .email(req.getEmail())
//                .phone(req.getPhone())
//                .dob(req.getDob())
//                .gender(req.getGender())
//                .address(req.getAddress())
//                .aadhaarMasked(mask(req.getAadhaar()))
//                .panMasked(mask(req.getPan()))
//                .accountNumber(accountNumber)
//                .passwordHash(passwordHash)
//                .ifscCode(branch.getIfscCode())
//                .nomineeName(req.getNominee() != null
//                        ? req.getNominee().getName() : null)
//                .nomineeRelation(req.getNominee() != null
//                        ? req.getNominee().getRelation() : null)
//                .nomineeDob(req.getNominee() != null
//                        ? req.getNominee().getDob() : null)
//                .status(CustomerStatus.ACTIVE)
//                .kycStatus(KycStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        Customer saved = customerRepo.save(customer);
//
//        if (req.getNominee() != null) {
//            nomineeRepository.save(
//                    Nominee.builder()
//                            .customerId(saved.getId())
//                            .name(req.getNominee().getName())
//                            .relation(req.getNominee().getRelation())
//                            .dob(req.getNominee().getDob())
//                            .build()
//            );
//        }
//
//        audit("CUSTOMER_CREATED", saved.getId(), "Customer created by admin");
//
//
//        syncWithAccountService(
//                accountNumber,
//                saved.getId(),
//                req.getAccountType() != null ? req.getAccountType() : "SAVINGS",
//                passwordHash,
//                branch.getIfscCode()
//        );
//
//        return CreateCustomerAccountResponse.builder()
//                .success(true)
//                .customerId(saved.getId().toString())
//                .accountNumber(accountNumber)
//                .password(tempPassword)
//                .bankName(branch.getBankName())
//                .branchName(branch.getBranchName())
//                .ifscCode(branch.getIfscCode())
//                .kycStatus("PENDING")
//                .build();
//    }
//
//
//    private void syncWithAccountService(
//            String accountNumber,
//            UUID customerId,
//            String accountType,
//            String passwordHash,
//            String ifscCode) {
//
//        try {
//            AccountSyncRequest request = AccountSyncRequest.builder()
//                    .accountNumber(accountNumber)
//                    .customerId(customerId.toString())
//                    .accountType(accountType.toUpperCase())
//                    .passwordHash(passwordHash)
//                    .status("ACTIVE")
//                    .balance(0.0)
//                    .primaryAccount(true)
//                    .ifscCode(ifscCode)
//                    .build();
//
//            System.out.println(" FEIGN → IFSC SENDING = " + ifscCode);
//
//            accountClient.createAccount(request);
//
//            System.out.println("✅Account created via FEIGN");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw BusinessException.internal(
//                    "Customer created but account creation failed"
//            );
//        }
//    }

    @Override
    public List<AdminCustomerSummary> getAllCustomers() {
        return customerRepo.findAll().stream()
                .filter(c -> c.getStatus() != CustomerStatus.DELETED)
                .map(this::mapSummary)
                .toList();
    }

    @Override
    public AdminCustomerDetail getCustomerById(UUID customerId) {
        return mapDetail(getCustomer(customerId));
    }


    @Override
    public KycApprovalResponse approveOrRejectKyc(
            UUID customerId,
            KycApprovalRequest req
    ) {

        Customer customer = getCustomer(customerId);

        CustomerValidator.validateKyc(customer, req);

        customer.setKycStatus(req.getStatus());
        customer.setKycVerifiedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        audit("KYC_" + (req.getStatus() != null ? req.getStatus().name() : "UNKNOWN"), customerId, req.getRemarks() != null ? req.getRemarks() : "No remarks");
        return new KycApprovalResponse(
                customer.getId(),
                customer.getKycVerifiedAt()
        );
    }

    @Override
    public AdminCustomerDetail blockCustomer(UUID customerId, String reason) {

        Customer customer = getCustomer(customerId);

        CustomerValidator.validateBlock(customer, reason);

        customer.setStatus(CustomerStatus.BLOCKED);
        customer.setUpdatedAt(LocalDateTime.now());

        audit("CUSTOMER_BLOCKED", customerId, reason);
        return mapDetail(customer);
    }

    @Override
    public AdminCustomerDetail unblockCustomer(UUID customerId) {

        Customer customer = getCustomer(customerId);

        CustomerValidator.validateUnblock(customer);

        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());

        audit("CUSTOMER_UNBLOCKED", customerId, "Unblocked");
        return mapDetail(customer);
    }

    @Override
    public void updateCustomer(
            UUID customerId,
            UpdateCustomerRequest req
    ) {

        Customer customer = getCustomer(customerId);

        CustomerValidator.validateUpdate(customer, req);

        if (req.getEmail() != null) customer.setEmail(req.getEmail());
        if (req.getPhone() != null) customer.setPhone(req.getPhone());
        if (req.getAddress() != null) customer.setAddress(req.getAddress());

        customer.setUpdatedAt(LocalDateTime.now());
        audit("CUSTOMER_UPDATED", customerId, "Profile updated");

        mapDetail(customer);
    }

    @Override
    public void deleteCustomer(UUID customerId) {

        Customer customer = getCustomer(customerId);

        CustomerValidator.validateDelete(customer);

        customer.setStatus(CustomerStatus.DELETED);
        audit("CUSTOMER_DELETED", customerId, "Soft delete");
    }


    private Customer getCustomer(UUID id) {
        return customerRepo.findById(id)
                .orElseThrow(BusinessException::customerNotFound);
    }

//    public CustomerSummary getCustomerSummary(UUID customerId) {
//
//        Customer c = customerRepo.findById(customerId)
//                .orElseThrow(BusinessException::customerNotFound);
//
//        return CustomerSummary.builder()
//                .customerId(c.getId())
//                .fullName(c.getFullName())
//                .kycStatus(c.getKycStatus().name())
//                .nomineeName(c.getNomineeName())
//                .nomineeRelation(c.getNomineeRelation())
//                .build();
//    }

    private void audit(String action, UUID customerId, String reason) {
        auditRepo.save(CustomerAudit.builder()
                .customerId(customerId)
                .action(action)
                .reason(reason)
                .performedBy("ADMIN")
                .timestamp(LocalDateTime.now())
                .build());
    }

    private AdminCustomerSummary mapSummary(Customer c) {
        return AdminCustomerSummary.builder()
                .customerId(c.getId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .status(c.getStatus().name())
                .kycStatus(c.getKycStatus().name())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private AdminCustomerDetail mapDetail(Customer c) {
        return AdminCustomerDetail.builder()
                .customerId(c.getId())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .dob(c.getDob())
                .gender(c.getGender())
                .address(c.getAddress())
                .status(c.getStatus())
                .kycStatus(c.getKycStatus())
                .kycVerifiedAt(c.getKycVerifiedAt())
                .createdAt(c.getCreatedAt())
                .build();
    }
//
//    private String generateIfsc(String bank, String city) {
//
//        String bankCode = bank.replaceAll("\\s+", "")
//                .toUpperCase()
//                .substring(0, 4);
//
//        String cityCode = city.replaceAll("\\s+", "")
//                .toUpperCase()
//                .substring(0, 3);
//
//        int count = bankBranchRepo
//                .countByBankNameAndCity(bank, city);
//
//        String seq = String.format("%02d", count + 1);
//
//        return bankCode + "0" + cityCode + seq;
//    }
//
//    private String generateAccountNumber() {
//        return "AC" + System.currentTimeMillis();
//    }
//
//    private String generateTempPassword() {
//        return "Temp@" + (1000 + new Random().nextInt(9000));
//    }
//
//    private String mask(String value) {
//        if (value == null || value.length() < 4) return "****";
//        return "****-****-" + value.substring(value.length() - 4);
//    }
}
