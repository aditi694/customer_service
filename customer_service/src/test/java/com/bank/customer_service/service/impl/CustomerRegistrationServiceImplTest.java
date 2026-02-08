package com.bank.customer_service.service.impl;

import com.bank.customer_service.dto.NomineeDto;
import com.bank.customer_service.dto.client.AccountClient;
import com.bank.customer_service.dto.request.AccountSyncRequest;
import com.bank.customer_service.dto.request.CustomerRegistrationRequest;
import com.bank.customer_service.dto.response.CustomerRegistrationResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.repository.NomineeRepository;
import com.bank.customer_service.service.impl.CustomerRegistrationServiceImpl;
import com.bank.customer_service.validation.CustomerValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceImplTest {

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private BankBranchRepository bankBranchRepo;

    @Mock
    private NomineeRepository nomineeRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CustomerRegistrationServiceImpl service;

    private CustomerRegistrationRequest baseRequest;
    private BankBranch branch;

    @BeforeEach
    void setUp() {

        baseRequest = CustomerRegistrationRequest.builder()
                .name("Aditi Goel")
                .email("aditi@test.com")
                .phone("9999999999")
                .password("password123")
                .confirmPassword("password123")
                .dob(LocalDate.of(2000, 1, 1))
                .gender("FEMALE")
                .address("Delhi")
                .aadhaar("123456789012")
                .pan("ABCDE1234F")
                .preferredBankName("HDFC Bank")
                .preferredCity("Delhi")
                .preferredBranchName("Connaught Place")
                .accountType("SAVINGS")
                .build();

        branch = BankBranch.builder()
                .bankName("HDFC Bank")
                .city("Delhi")
                .branchName("Connaught Place")
                .ifscCode("HDFC0DEL01")
                .build();
    }
    @Test
    void customerRegister_success_noNominne_branch() {
        UUID customerId = UUID.randomUUID();
        Customer savedCustomer = Customer.builder()
                .id(customerId)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .passwordHash("hashedPwd")
                .ifscCode("HDFC0DEL01")
                .build();

        try (MockedStatic<CustomerValidator> mocked = mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenAnswer(inv -> null);

            when(bankBranchRepo.findByBankNameAndCityAndBranchName(any(), any(), any()))
                    .thenReturn(Optional.of(branch));
            when(passwordEncoder.encode(any()))
                    .thenReturn("hashedPwd");
            when(customerRepo.save(any(Customer.class)))
                    .thenReturn(savedCustomer);

            CustomerRegistrationResponse response = service.registerCustomer((baseRequest));

            Assertions.assertEquals(customerId.toString(), response.getCustomerId());
            Assertions.assertTrue(response.isSuccess());
            Assertions.assertEquals("HDFC0DEL01", response.getIfscCode());

            verify(customerRepo).save(any());
            verify(accountClient).createAccount(any());
            verify(nomineeRepo, never()).save(any());
            verify(bankBranchRepo, never()).save(any());
        }
    }
    @Test
    void customerRegister_success_withNominee(){
        NomineeDto nominee = new NomineeDto();
        nominee.setName("Father");
        nominee.setRelation("FATHER");
        nominee.setDob(LocalDate.of(1970, 1, 1));

        CustomerRegistrationRequest request =
                baseRequest.toBuilder().nominee(nominee).build();

        UUID customerId = UUID.randomUUID();
        Customer savedCustomer = Customer.builder()
                .id(customerId)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .passwordHash("hashedPwd")
                .ifscCode("HDFC0DEL01")
                .build();

        try (MockedStatic<CustomerValidator> mocked = mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenAnswer(inv -> null);

            when(bankBranchRepo.findByBankNameAndCityAndBranchName(any(), any(), any()))
                    .thenReturn(Optional.of(branch));
            when(passwordEncoder.encode(any()))
                    .thenReturn("hashedPwd");
            when(customerRepo.save(any(Customer.class)))
                    .thenReturn(savedCustomer);

            CustomerRegistrationResponse response = service.registerCustomer((request));

            Assertions.assertTrue(response.isSuccess());

            verify(customerRepo).save(any());
            verify(nomineeRepo).save(any());
            verify(accountClient).createAccount(any());
        }
    }
    @Test
    void customer_branchNotExist_createNewBranch() {
        UUID customerID = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerID)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .passwordHash("hashedPwd")
                .ifscCode("HDFC0DEL01")
                .build();

        try (MockedStatic<CustomerValidator> mocked =
                     mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenAnswer(inv -> null);

            when(bankBranchRepo.findByBankNameAndCityAndBranchName(any(), any(), any()))
                    .thenReturn(Optional.empty());

            when(bankBranchRepo.countByBankNameAndCity(
                    "HDFC Bank", "Delhi"))
                    .thenReturn(5);

            when(bankBranchRepo.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            when(passwordEncoder.encode(any()))
                    .thenReturn("hashedPwd");

            when(customerRepo.save(any(Customer.class)))
                    .thenReturn(customer);

            CustomerRegistrationResponse response =
                    service.registerCustomer(baseRequest);

            assertTrue(response.getIfscCode().startsWith("HDFC0DEL"));
            assertTrue(response.getIfscCode().endsWith("06"));

            verify(bankBranchRepo).countByBankNameAndCity("HDFC Bank", "Delhi");
            verify(bankBranchRepo).save(any());
        }
    }

    @Test
    void registerCustomer_validationFailure() {

        try (MockedStatic<CustomerValidator> mocked = mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenThrow(BusinessException.badRequest("Invalid"));

            assertThrows(BusinessException.class,
                    () -> service.registerCustomer(baseRequest));

            verify(customerRepo, never()).save(any());
            verify(accountClient, never()).createAccount(any());
            verify(nomineeRepo, never()).save(any());
        }
    }
    @Test
    void registerCustomer_accountServiceFailure() {
        UUID customerID = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerID)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .passwordHash("hasedPwd")
                .ifscCode("HDFC0DEL01")
                .build();

        try (MockedStatic<CustomerValidator> mocked = mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenAnswer(inv -> null);

            when(bankBranchRepo.findByBankNameAndCityAndBranchName(any(), any(), any()))
                    .thenReturn(Optional.of(branch));

            when(passwordEncoder.encode(any()))
                    .thenReturn("hashedPwd");

            when(customerRepo.save(any(Customer.class)))
                    .thenReturn(customer);

            doThrow(new RuntimeException("Feign down"))
                    .when(accountClient).createAccount(any());

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> service.registerCustomer(baseRequest)
            );

            assertTrue(ex.getMessage()
                    .contains("Customer created but account creation failed"));

            verify(customerRepo).save(any());
            verify(accountClient).createAccount(any());
        }
    }
    @Test
    void registerCustomer_passwordEncoded() {
        UUID customerID = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerID)
                .fullName("Aditi Goel")
                .email("aditi@test.com")
                .passwordHash("hasedPwd")
                .ifscCode("HDFC0DEL01")
                .build();

        try (MockedStatic<CustomerValidator> mocked = mockStatic(CustomerValidator.class)) {

            mocked.when(() -> CustomerValidator.validateRegistration(any(), any()))
                    .thenAnswer(inv -> null);

            when(bankBranchRepo.findByBankNameAndCityAndBranchName(any(), any(), any()))
                    .thenReturn(Optional.of(branch));

            when(passwordEncoder.encode("password123"))
                    .thenReturn("hashedPwd");

            when(customerRepo.save(any(Customer.class)))
                    .thenReturn(customer);

            doNothing().when(accountClient).createAccount(any());

            service.registerCustomer(baseRequest);

            ArgumentCaptor<Customer> captor =
                    ArgumentCaptor.forClass(Customer.class);
            verify(customerRepo).save(captor.capture());

            assertEquals("hashedPwd",
                    captor.getValue().getPasswordHash());

            verify(passwordEncoder).encode("password123");
        }
    }
}