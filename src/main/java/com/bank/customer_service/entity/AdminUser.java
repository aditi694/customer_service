package com.bank.customer_service.entity;

import com.bank.customer_service.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Table(name = "admin_users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String username;

    private String password;
}
