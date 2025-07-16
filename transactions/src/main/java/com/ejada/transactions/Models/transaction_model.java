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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class transaction_model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "from_account_id")
    private UUID from_account_id;
    @Column(name = "to_account_id")
    private UUID to_account_id;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "status")
    private Status status = Status.INITIATED;
    @Column(name = "created_at")
    private Timestamp created_at = Timestamp.from(Instant.now());

}
