package com.bank.customer_service.repository;

import com.bank.customer_service.entity.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankBranchRepository extends JpaRepository<BankBranch, Long> {

    Optional<BankBranch> findByIfscCode(String ifscCode);

    boolean existsByIfscCode(String ifscCode);

    Optional<BankBranch> findByBankNameAndCityAndBranchName(String preferredBankName, String preferredCity, String preferredBranchName);
}