package com.bank.customer_service.entity;

import com.bank.customer_service.enums.CustomerStatus;
import com.bank.customer_service.enums.KycMethod;
import com.bank.customer_service.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;
    private LocalDate dob;
    private String gender;
    private String address;

    private String aadhaarMasked;
    private String panMasked;
    @Column(unique = true)
    private String accountNumber;
    private String passwordHash;

    private String nomineeName;
    private String nomineeRelation;
    private LocalDate nomineeDob;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;
    private LocalDateTime kycVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "ifsc_code")
    private String ifscCode;

}
