package com.ejada.transactions.dto;

import com.ejada.transactions.Models.TransactionModel;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {
    
    public static TransactionResponse toTransactionResponse(TransactionModel transaction) {
        if (transaction == null) {
            return null;
        }
        
        return new TransactionResponse(
            transaction.getId(),
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getStatus(),
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
