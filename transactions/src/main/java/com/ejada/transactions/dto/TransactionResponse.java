package com.ejada.transactions.dto;

import com.ejada.transactions.Models.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private UUID transactionId;
    private UUID fromAccountId;
    private UUID toAccountId;
    private Double amount;
    private String description;
    private TransactionStatus status;
    private Timestamp createdAt;
}
