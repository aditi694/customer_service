package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.dto.client.AccountClient;
import com.bank.customer_service.dto.request.AccountSyncRequest;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.entity.Nominee;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.NomineeRepository;
import com.bank.customer_service.service.CustomerRegistrationService;
import com.bank.customer_service.validation.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

    private final CustomerRepository customerRepo;
    private final BankBranchRepository bankBranchRepo;
    private final NomineeRepository nomineeRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccountClient accountClient;

    private static final String ACCOUNT_SERVICE_URL =
            "http://localhost:8082/api/internal/accounts/create";

    @Override
    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest req) {

//        log.info("Customer registration started for email: {}", req.getEmail());

        CustomerValidator.validateRegistration(req, customerRepo);
        BankBranch branch = resolveBankBranch(req);
        String accountNumber = generateAccountNumber();
        String passwordHash = passwordEncoder.encode(req.getPassword());
        Customer customer = Customer.builder()
                .fullName(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .dob(req.getDob())
                .gender(req.getGender())
                .address(req.getAddress())
                .aadhaarMasked(maskAadhaar(req.getAadhaar()))
                .panMasked(maskPan(req.getPan()))
                .accountNumber(accountNumber)
                .passwordHash(passwordHash)
                .ifscCode(branch.getIfscCode())
                .nomineeName(req.getNominee() != null ? req.getNominee().getName() : null)
                .nomineeRelation(req.getNominee() != null ? req.getNominee().getRelation() : null)
                .nomineeDob(req.getNominee() != null ? req.getNominee().getDob() : null)
                .status(CustomerStatus.ACTIVE)
                .kycStatus(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepo.save(customer);

        log.info("Customer created with ID: {}", savedCustomer.getId());

        if (req.getNominee() != null) {
            saveNominee(savedCustomer.getId(), req.getNominee());
        }

        syncWithAccountService(
                accountNumber,
                savedCustomer.getId().toString(),
                req.getAccountType() != null ? req.getAccountType() : "SAVINGS",
                passwordHash,
                branch.getIfscCode()
        );

//        log.info("Customer registration completed successfully");

        return CustomerRegistrationResponse.builder()
                .success(true)
                .message("Registration successful! Your account has been created.")
                .customerId(savedCustomer.getId().toString())
                .accountNumber(accountNumber)
                .ifscCode(branch.getIfscCode())
                .bankName(branch.getBankName())
                .branchName(branch.getBranchName())
                .kycStatus("PENDING")
                .accountStatus("ACTIVE")
                .loginUrl("/api/account/login")
                .loginInstructions(buildLoginInstructions(accountNumber))
                .nextSteps("Complete KYC verification to unlock all banking features. You will be notified once approved.")
                .build();
    }

    private String buildLoginInstructions(String accountNumber) {
        return String.format(
                "You can now login using:\n" +
                        "• Account Number: %s\n" +
                        "• Password: (the password you just created)\n" +
                        "• Login endpoint: POST /api/account/login",
                accountNumber
        );
    }

    private BankBranch resolveBankBranch(CustomerRegistrationRequest req) {
        return bankBranchRepo
                .findByBankNameAndCityAndBranchName(
                        req.getPreferredBankName(),
                        req.getPreferredCity(),
                        req.getPreferredBranchName()
                )
                .orElseGet(() -> {
                    String ifsc = generateIfsc(
                            req.getPreferredBankName(),
                            req.getPreferredCity()
                    );

                    return bankBranchRepo.save(
                            BankBranch.builder()
                                    .bankName(req.getPreferredBankName())
                                    .city(req.getPreferredCity())
                                    .branchName(req.getPreferredBranchName())
                                    .ifscCode(ifsc)
                                    .address(req.getAddress())
                                    .build()
                    );
                });
    }

    private void saveNominee(java.util.UUID customerId, NomineeDto dto) {
        nomineeRepo.save(
                Nominee.builder()
                        .customerId(customerId)
                        .name(dto.getName())
                        .relation(dto.getRelation())
                        .dob(dto.getDob())
                        .build()
        );
        log.info("Nominee saved for customer: {}", customerId);
    }

    private void syncWithAccountService(
            String accountNumber,
            String customerId,
            String accountType,
            String passwordHash,
            String ifscCode) {

        try {
            AccountSyncRequest request = AccountSyncRequest.builder()
                    .accountNumber(accountNumber)
                    .customerId(customerId)
                    .accountType(accountType.toUpperCase())
                    .passwordHash(passwordHash)
                    .status("ACTIVE")
                    .balance(0.0)
                    .primaryAccount(true)
                    .ifscCode(ifscCode)
                    .build();

            log.error("FEIGN → Sending IFSC = {}", ifscCode);

            accountClient.createAccount(request);

            log.info("✅ Account synced via Feign successfully");

        } catch (Exception e) {
            log.error("❌ Feign failed while syncing account", e);
            throw BusinessException.internal(
                    "Customer created but account creation failed"
            );
        }
    }


    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }

    private String generateIfsc(String bank, String city) {
        String bankCode = bank.replaceAll("\\s+", "")
                .toUpperCase()
                .substring(0, Math.min(4, bank.length()));

        String cityCode = city.replaceAll("\\s+", "")
                .toUpperCase()
                .substring(0, Math.min(3, city.length()));

        int count = bankBranchRepo.countByBankNameAndCity(bank, city);
        String seq = String.format("%02d", count + 1);

        return bankCode + "0" + cityCode + seq;
    }

    private String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) return "****";
        return "****-****-" + aadhaar.substring(aadhaar.length() - 4);
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return "****";
        return pan.substring(0, 2) + "XXXXX" + pan.substring(pan.length() - 1);
    }
}