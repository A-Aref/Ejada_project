package com.ejada.accounts.dto;

import com.ejada.accounts.Models.AccountStatus;
import com.ejada.accounts.Models.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private UUID accountId;
    private String accountNumber;
    private AccountType accountType;
    private Double balance;
    private AccountStatus status;
}
