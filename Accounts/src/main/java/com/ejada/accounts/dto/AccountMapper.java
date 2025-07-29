package com.ejada.accounts.dto;

import com.ejada.accounts.Models.AccountModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountMapper {
    
    public static AccountResponse toAccountResponse(AccountModel account) {
        if (account == null) {
            return null;
        }
        
        return new AccountResponse(
            account.getId(),
            account.getUserId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getBalance(),
            account.getStatus(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
    
    public static List<AccountResponse> toAccountResponseList(List<AccountModel> accounts) {
        if (accounts == null) {
            return null;
        }
        
        return accounts.stream()
                .map(AccountMapper::toAccountResponse)
                .collect(Collectors.toList());
    }
    
    public static AccountListResponse toAccountListResponse(List<AccountModel> accounts) {
        if (accounts == null) {
            return new AccountListResponse(null);
        }
        
        List<AccountResponse> accountResponses = toAccountResponseList(accounts);
        return new AccountListResponse(accountResponses);
    }
    
    public static TransferResponse toTransferResponse(String message, UUID fromAccountId, UUID toAccountId, Double amount) {
        return new TransferResponse(message, fromAccountId, toAccountId, amount);
    }
}
