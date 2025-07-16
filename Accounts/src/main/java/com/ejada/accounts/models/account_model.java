package com.ejada.accounts.models;

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
@Table(name = "accounts")
@Getter
@Setter
public class account_model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "user_id")
    private UUID user_id;
    @Column(name = "account_number", unique = true)
    private String account_number;
    @Column(name = "account_type")
    private Accounttype account_type;
    @Column(name = "balance")
    private Double balance = 0.00;
    @Column(name = "status")
    private Accountstatus status = Accountstatus.ACTIVE;
    @Column(name = "created_at")
     private Timestamp created_at = Timestamp.from(Instant.now());
    @Column(name = "updated_at")
    private Timestamp update_at = Timestamp.from(Instant.now());


}
