package com.bank.customer_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nominee")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nominee {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID customerId;
    private String name;
    private String relation;
    private LocalDate dob;
}
