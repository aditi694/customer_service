package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.enums.CustomerStatus;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Customer> findByAccountNumber(String accountNumber);

    @Query("SELECT c FROM Customer c WHERE UPPER(c.accountNumber) = UPPER(:accountNumber)")
    Optional<Customer> findByAccountNumberIgnoreCase(@Param("accountNumber") String accountNumber);
}
