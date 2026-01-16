package com.bank.customer_service.entity;

import com.bank.customer_service.enums.Role;
import com.bank.customer_service.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 🔗 LINK TO CUSTOMER
    @Column(name = "customer_id", columnDefinition = "BINARY(16)")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

}
