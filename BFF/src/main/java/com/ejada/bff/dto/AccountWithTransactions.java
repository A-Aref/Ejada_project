package com.ejada.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountWithTransactions {
    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private String status;
    private List<Transaction> accountTransactions;
}
