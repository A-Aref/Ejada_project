package com.ejada.accounts.Models;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "account_number", unique = true)
    private String accountNumber;
    @Column(name = "account_type")
    private AccountType accountType;
    @Column(name = "balance")
    private Double balance = 0.00;
    @Column(name = "status")
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name = "created_at")
     private Timestamp createdAt = Timestamp.from(Instant.now());
    @Column(name = "updated_at")
    private Timestamp updatedAt = Timestamp.from(Instant.now());


}
