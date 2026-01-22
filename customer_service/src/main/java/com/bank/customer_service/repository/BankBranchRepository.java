package com.bank.customer_service.repository;

import com.bank.customer_service.entity.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankBranchRepository
        extends JpaRepository<BankBranch, Long> {

    Optional<BankBranch> findByBankNameAndCityAndBranchName(
            String bankName,
            String city,
            String branchName
    );

    int countByBankNameAndCity(String bankName, String city);
}
