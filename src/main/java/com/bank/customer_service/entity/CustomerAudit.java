package com.bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "customer_audit")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CustomerAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID customerId;
    private String action;
    private String reason;
    private String performedBy;
    private LocalDateTime timestamp;
}

