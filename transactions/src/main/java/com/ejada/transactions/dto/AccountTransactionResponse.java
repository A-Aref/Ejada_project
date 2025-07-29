package com.ejada.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionResponse {
    
    private UUID transactionId;
    private UUID accountId;
    private Double amount;
    private String description;
    private Timestamp timestamp;
}
