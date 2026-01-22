package com.bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bank_branch",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bank_name", "city", "branch_name"})
        }
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

    private String bankName;
    private String city;
    private String branchName;

    @Column(nullable = false, unique = true)
    private String ifscCode;
}
