package com.ejada.transactions.Services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.transactions.Models.TransactionStatus;
import com.ejada.transactions.dto.TransactionExecutionRequest;
import com.ejada.transactions.dto.TransactionListResponse;
import com.ejada.transactions.dto.TransactionMapper;
import com.ejada.transactions.dto.TransactionRequest;
import com.ejada.transactions.dto.TransactionResponse;
import com.ejada.transactions.exception.AccountServiceException;
import com.ejada.transactions.exception.InvalidTransactionException;
import com.ejada.transactions.exception.TransactionExecutionException;
import com.ejada.transactions.exception.TransactionNotFoundException;
import com.ejada.transactions.Models.TransactionModel;
import com.ejada.transactions.Repos.TransactionRepo;
import com.ejada.transactions.Services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired
    private WebClient webClientAccounts;
    
    @Override
    public TransactionResponse getTransaction(UUID transactionId) {
        TransactionModel transaction = transactionRepo.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
        return TransactionMapper.toTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse getLatestTransaction(UUID accountId) {
        TransactionModel transaction = transactionRepo.findFirstByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId);
        if (transaction == null) {
            throw new TransactionNotFoundException("No transactions found for account ID: " + accountId);
        }
        return TransactionMapper.toTransactionResponse(transaction);
    }

    @Override
    public TransactionListResponse getTransactions(UUID accountId) {
        List<TransactionModel> transactions = transactionRepo.findByFromAccountIdOrToAccountId(accountId, accountId);
        return TransactionMapper.toTransactionListResponse(transactions);
    }
    
    @Override
    public TransactionResponse initiateTransaction(TransactionRequest request) {
        if (request.getAmount() <= 0) {
            throw new InvalidTransactionException("Transaction amount must be positive");
        }
        
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new InvalidTransactionException("From account and to account cannot be the same");
        }
        
        TransactionModel newTransaction = TransactionMapper.toTransactionModel(request);
        newTransaction.setStatus(TransactionStatus.INITIATED);
        newTransaction.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        
        TransactionModel savedTransaction = transactionRepo.save(newTransaction);
        return TransactionMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse initiateTransactionWithValidation(TransactionRequest request) {
        // Validate accounts exist by calling account service
        try {
            ResponseEntity<Object> fromAccount = webClientAccounts.get()
                    .uri("/{accountId}", request.getFromAccountId())
                    .retrieve().toEntity(Object.class).block();
            ResponseEntity<Object> toAccount = webClientAccounts.get()
                    .uri("/{accountId}", request.getToAccountId())
                    .retrieve().toEntity(Object.class).block();
                    
            if (fromAccount == null || toAccount == null || 
                fromAccount.getStatusCode().isError() || toAccount.getStatusCode().isError()) {
                throw new AccountServiceException("Invalid accounts or account service unavailable");
            }
        } catch (Exception e) {
            throw new AccountServiceException("Failed to validate accounts: " + e.getMessage());
        }
        
        // Call the existing initiate transaction method
        return initiateTransaction(request);
    }

    @Override
    public TransactionResponse executeTransaction(UUID transactionId) {
        TransactionModel transaction = transactionRepo.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
            
        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            throw new InvalidTransactionException("Transaction is not in INITIATED status");
        }
        
        transaction.setStatus(TransactionStatus.SUCCESS);
        TransactionModel savedTransaction = transactionRepo.save(transaction);
        return TransactionMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse cancelTransaction(UUID transactionId) {
        TransactionModel transaction = transactionRepo.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
            
        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            throw new InvalidTransactionException("Transaction is not in INITIATED status and cannot be cancelled");
        }
        
        transaction.setStatus(TransactionStatus.FAILED);
        TransactionModel savedTransaction = transactionRepo.save(transaction);
        return TransactionMapper.toTransactionResponse(savedTransaction);
    }
    
    @Override
    public TransactionResponse executeTransfer(TransactionExecutionRequest request) {
        // Get transaction model for account service interaction
        TransactionModel transaction = getTransactionModel(request.getTransactionId());
        
        if (transaction.getStatus() == TransactionStatus.SUCCESS) {
            throw new TransactionExecutionException("Transaction already executed");
        }
        
        // Execute transfer with account service
        try {
            ResponseEntity<Object> executeTransfer = webClientAccounts.put().uri("/transfer")
                    .bodyValue(new HashMap<String, Object>() {
                        {
                            put("fromAccountId", transaction.getFromAccountId());
                            put("toAccountId", transaction.getToAccountId());
                            put("amount", transaction.getAmount());
                        }
                    })
                    .retrieve().toEntity(Object.class).block();

            if (executeTransfer == null || executeTransfer.getStatusCode().isError()) {
                // Cancel transaction and throw exception
                cancelTransaction(transaction.getId());
                throw new TransactionExecutionException("Transfer failed in account service");
            }
        } catch (Exception e) {
            // Cancel transaction and re-throw
            cancelTransaction(transaction.getId());
            throw new TransactionExecutionException("Transfer execution failed: " + e.getMessage());
        }
        
        // Execute transaction (mark as successful)
        return executeTransaction(transaction.getId());
    }
    
    @Override
    public TransactionModel getTransactionModel(UUID transactionId) {
        return transactionRepo.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
    }
}
