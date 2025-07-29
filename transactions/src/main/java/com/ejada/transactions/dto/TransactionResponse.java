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
    private TransactionStatus status;
    private Timestamp createdAt;
}
