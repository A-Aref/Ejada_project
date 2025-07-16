package com.ejada.transactions.Models;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "from_account_id")
    @NotNull
    private UUID fromAccountId;
    @Column(name = "to_account_id")
    @NotNull
    private UUID toAccountId;
    @Column(name = "amount")
    @NotNull
    private Double amount;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'INITIATED'")
    private TransactionStatus status;
    @Column(name = "created_at")
    @NotNull
    @CreationTimestamp
    private Timestamp createdAt;

}
