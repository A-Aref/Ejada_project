package com.ejada.transactions.Models;

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
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "from_account_id")
    private UUID fromAccountId;
    @Column(name = "to_account_id")
    private UUID toAccountId;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "status")
    private TransactionStatus status = TransactionStatus.INITIATED;
    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.from(Instant.now());

}
