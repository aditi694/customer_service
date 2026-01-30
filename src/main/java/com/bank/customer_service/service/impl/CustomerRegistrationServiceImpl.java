package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.client.AccountClient;
import com.bank.customer_service.dto.request.AccountSyncRequest;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.CustomerRegistrationService;
import com.bank.customer_service.util.MaskingUtil;
import com.bank.customer_service.validation.CustomerValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

    private final CustomerRepository customerRepo;
    private final BankBranchRepository bankBranchRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccountClient accountClient;

    @Override
    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest req) {

        log.info("Customer registration started for email={}", req.getEmail());

        // 1️⃣ Validate request
        CustomerValidator.validateRegistration(req, customerRepo);

        // 2️⃣ Resolve Bank Branch (DB SOURCE OF TRUTH)
        BankBranch branch = resolveBranch(req);

        // 3️⃣ Generate identifiers
        String accountNumber = generateAccountNumber();
        String passwordHash = passwordEncoder.encode(req.getPassword());

        // 4️⃣ Create Customer
        Customer customer = Customer.builder()
                .fullName(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .dob(req.getDob())
                .gender(req.getGender())
                .address(req.getAddress())
                .aadhaarMasked(MaskingUtil.maskAadhaar(req.getAadhaar()))
                .panMasked(MaskingUtil.maskPan(req.getPan()))
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

        Customer saved = customerRepo.save(customer);

        // 5️⃣ Sync with Account Service
        syncWithAccountService(saved, req.getAccountType());

        // 6️⃣ Response
        return CustomerRegistrationResponse.builder()
                .success(true)
                .message("🎉 Registration successful! Your account has been created.")
                .customerId(saved.getId().toString())
                .accountNumber(accountNumber)
                .ifscCode(branch.getIfscCode())
                .bankName(branch.getBankName())
                .branchName(branch.getBranchName())
                .kycStatus(saved.getKycStatus().name())
                .accountStatus("ACTIVE")
                .loginUrl("/api/account/login")
                .loginInstructions(
                        "Login using:\n" +
                                "• Account Number: " + accountNumber + "\n" +
                                "• Password: (the one you created)\n" +
                                "• Endpoint: POST /api/account/login"
                )
                .nextSteps("Complete KYC verification to unlock all banking features.")
                .build();
    }

    // ========================= BANK BRANCH =========================

    private BankBranch resolveBranch(CustomerRegistrationRequest req) {

        return bankBranchRepo
                .findByBankNameAndCityAndBranchName(
                        req.getPreferredBankName(),
                        req.getPreferredCity(),
                        req.getPreferredBranchName()
                )
                .orElseGet(() -> {
                    String address = req.getPreferredBranchName() + ", " + req.getPreferredCity();
                    BankBranch branch = BankBranch.builder()
                            .address(address)
                            .bankName(req.getPreferredBankName())
                            .city(req.getPreferredCity())
                            .branchName(req.getPreferredBranchName())
                            .ifscCode(generateIfsc(req.getPreferredBankName(), req.getPreferredCity()))
                            .build();

                    return bankBranchRepo.save(branch);
                });
    }

    // ========================= ACCOUNT SYNC =========================

    private void syncWithAccountService(Customer customer, String accountType) {

        AccountSyncRequest request = AccountSyncRequest.builder()
                .accountNumber(customer.getAccountNumber())
                .customerId(customer.getId().toString())
                .accountType(accountType != null ? accountType.toUpperCase() : "SAVINGS")
                .passwordHash(customer.getPasswordHash())
                .status("ACTIVE")
                .balance(0.0)
                .primaryAccount(true)
                .ifscCode(customer.getIfscCode())
                .build();

        accountClient.createAccount(request);
    }

    // ========================= HELPERS =========================

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

        int random = (int) (Math.random() * 90 + 10);

        return bankCode + "0" + cityCode + random;
    }
}