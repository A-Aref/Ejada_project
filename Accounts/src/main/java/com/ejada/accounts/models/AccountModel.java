package com.ejada.accounts.Models;

import java.sql.Timestamp;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;
    @Column(name = "user_id")
    @NotNull
    private UUID userId;
    @Column(name = "account_number", unique = true,nullable = false)
    @NotNull
    private String accountNumber;
    @Column(name = "account_type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Column(name = "balance")
    @ColumnDefault("0.00")
    @NotNull
    private Double balance = 0.00;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    @NotNull
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;


    @PrePersist
    public void generateAccountNumber() {
        this.accountNumber = "ACCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


}
