package com.ejada.transactions.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionExecutionRequest {
    
    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;
}
