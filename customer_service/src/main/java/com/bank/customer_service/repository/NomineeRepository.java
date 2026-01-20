package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NomineeRepository extends JpaRepository<Nominee, UUID> {
    Optional<Nominee> findByCustomerId(UUID customerId);
}
