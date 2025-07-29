package com.ejada.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String message;
    private UUID fromAccountId;
    private UUID toAccountId;
    private Double amount;
}
