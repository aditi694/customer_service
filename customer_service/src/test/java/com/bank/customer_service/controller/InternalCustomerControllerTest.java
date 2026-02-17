package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CustomerDetailResponse;
import com.bank.customer_service.dto.CustomerSummary;
import com.bank.customer_service.dto.response.BankBranchResponse;
import com.bank.customer_service.entity.BankBranch;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.KycStatus;
import com.bank.customer_service.exception.BusinessException;
import com.bank.customer_service.repository.BankBranchRepository;
import com.bank.customer_service.repository.CustomerRepository;
import com.bank.customer_service.service.InternalCustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalCustomerControllerTest {

    @Mock
    private InternalCustomerService service;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private BankBranchRepository bankBranchRepo;

    @InjectMocks
    private InternalCustomerController controller;

    @Test
    void getCustomerDetail_success() {
        UUID id = UUID.randomUUID();
        CustomerDetailResponse responseDto = new CustomerDetailResponse();

        when(service.getCustomerDetails(id)).thenReturn(responseDto);

        CustomerDetailResponse response = controller.getCustomerDetail(id);

        Assertions.assertNotNull(response);
        verify(service).getCustomerDetails(id);
    }

    @Test
    void getCustomerSummary_success() {
        UUID id = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setFullName("John Doe");
        customer.setKycStatus(KycStatus.APPROVED);
        customer.setNomineeName("Jane");
        customer.setNomineeRelation("Sister");

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        CustomerSummary summary = controller.getCustomerSummary(id);

        Assertions.assertEquals("John Doe", summary.getFullName());
        Assertions.assertEquals("APPROVED", summary.getKycStatus());

        verify(customerRepo).findById(id);
    }

    @Test
    void getCustomerSummary_notFound() {
        UUID id = UUID.randomUUID();

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> controller.getCustomerSummary(id));
    }

    @Test
    void getCustomerContact_success() {
        UUID id = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setPhone("9999999999");

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        String phone = controller.getCustomerContact(id);

        Assertions.assertEquals("9999999999", phone);
    }

    @Test
    void getIfscByAccount_success() {
        String accountNumber = "12345";

        Customer customer = new Customer();
        customer.setIfscCode("IFSC001");

        when(customerRepo.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(customer));

        String ifsc = controller.getIfscByAccount(accountNumber);

        Assertions.assertEquals("IFSC001", ifsc);
    }

    @Test
    void getIfscByAccount_notFound() {
        when(customerRepo.findByAccountNumber("12345"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> controller.getIfscByAccount("12345"));
    }

    @Test
    void getBankBranch_success() {
        String ifsc = "IFSC001";

        BankBranch branch = new BankBranch();
        branch.setIfscCode(ifsc);
        branch.setBankName("ABC Bank");
        branch.setBranchName("Main Branch");
        branch.setCity("Mumbai");

        when(bankBranchRepo.findByIfscCode(ifsc))
                .thenReturn(Optional.of(branch));

        BankBranchResponse response = controller.getBankBranch(ifsc);

        Assertions.assertEquals("ABC Bank", response.getBankName());
        Assertions.assertEquals("Main Branch, Mumbai", response.getAddress());
    }

    @Test
    void getBankBranch_notFound() {
        when(bankBranchRepo.findByIfscCode("IFSC001"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> controller.getBankBranch("IFSC001"));
    }
}
