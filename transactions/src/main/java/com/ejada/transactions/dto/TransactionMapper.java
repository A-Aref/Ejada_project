package com.ejada.transactions.dto;

import com.ejada.transactions.Models.TransactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionMapper {
    
    public static TransactionResponse toTransactionResponse(TransactionModel transaction) {
        if (transaction == null) {
            return null;
        }
        
        return new TransactionResponse(
            transaction.getId(),
            transaction.getStatus(),
            transaction.getCreatedAt()
        );
    }
    
    public static AccountTransactionResponse toAccountTransactionResponse(TransactionModel transaction, UUID accountId) {
        if (transaction == null) {
            return null;
        }
        
        // Determine if this is a debit or credit for the account
        Double amount = transaction.getAmount();
        if (transaction.getFromAccountId().equals(accountId)) {
            amount = -amount; // Debit (negative amount)
        }
        
        return new AccountTransactionResponse(
            transaction.getId(),
            transaction.getFromAccountId().equals(accountId)?transaction.getToAccountId():transaction.getFromAccountId(),
            amount,
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }
    
    public static List<TransactionResponse> toTransactionResponseList(List<TransactionModel> transactions) {
        if (transactions == null) {
            return null;
        }
        
        return transactions.stream()
                .map(TransactionMapper::toTransactionResponse)
                .collect(Collectors.toList());
    }

    public static AccountTransactions toAccountTransactionResponseList(List<TransactionModel> transactions, UUID accountId) {
        if (transactions == null) {
            return new AccountTransactions(new ArrayList<>());
        }

        List<AccountTransactionResponse> responseList = transactions.stream()
                .map(transaction -> toAccountTransactionResponse(transaction, accountId))
                .collect(Collectors.toList());

        return new AccountTransactions(responseList);
    }


    public static TransactionListResponse toTransactionListResponse(List<TransactionModel> transactions) {
        if (transactions == null) {
            return new TransactionListResponse(null);
        }
        
        List<TransactionResponse> transactionResponses = toTransactionResponseList(transactions);
        return new TransactionListResponse(transactionResponses);
    }
    
    /**
     * Creates a TransactionModel from TransactionRequest for initiation
     */
    public static TransactionModel toTransactionModel(TransactionRequest request) {
        if (request == null) {
            return null;
        }
        
        TransactionModel transaction = new TransactionModel();
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        
        return transaction;
    }
    
    
}
