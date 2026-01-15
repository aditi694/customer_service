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
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, updatable = false)
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void changeStatus(CustomerStatus newStatus) {
        if (this.status == CustomerStatus.CLOSED) {
            throw new IllegalStateException("Closed customer cannot be modified");
        }
        this.status = newStatus;
    }

    private String panMasked;
    private String aadhaarMasked;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private LocalDateTime kycVerifiedAt;

    public void verifyKyc(KycStatus status, KycMethod method) {

        if (this.kycStatus != KycStatus.PENDING) {
            throw new IllegalStateException("KYC already processed");
        }

        this.kycStatus = status;
        this.kycMethod = method;
        this.kycVerifiedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private KycMethod kycMethod;
}
