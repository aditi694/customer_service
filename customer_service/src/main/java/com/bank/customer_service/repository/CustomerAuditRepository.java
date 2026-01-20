package com.bank.customer_service.repository;

import com.bank.customer_service.entity.CustomerAudit;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, Long> {}
