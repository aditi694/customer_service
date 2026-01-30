package com.bank.customer_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bank_branch",
        uniqueConstraints = @UniqueConstraint(columnNames = "ifscCode")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, unique = true)
    private String ifscCode;

    @Column(nullable = false)
    private String address;
}