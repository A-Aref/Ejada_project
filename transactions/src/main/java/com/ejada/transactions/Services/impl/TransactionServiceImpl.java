package com.ejada.transactions.Services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.ejada.transactions.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.transactions.Models.TransactionStatus;
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
        try {
            webClientAccounts.get()
                    .uri("/{accountId}", accountId)
                    .retrieve()
                    .toEntity(Object.class).block();
        }catch(Exception e){
            throw new AccountServiceException("Failed to validate account: " + e.getMessage());
        }

        TransactionModel transaction = transactionRepo.findFirstByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId);
        if (transaction == null) {
            throw new TransactionNotFoundException("No transactions found for account ID: " + accountId);
        }
        return TransactionMapper.toTransactionResponse(transaction);
    }

    @Override
    public AccountTransactions getTransactions(UUID accountId) {
        try {
            webClientAccounts.get()
                    .uri("/{accountId}", accountId)
                    .retrieve()
                    .toEntity(Object.class).block();
        }catch(Exception e){
            throw new AccountServiceException("Failed to validate account: " + e.getMessage());
        }

        List<TransactionModel> transactions = transactionRepo.findByFromAccountIdOrToAccountId(accountId, accountId);
        if (transactions == null || transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for account ID: " + accountId);
        }

        return TransactionMapper.toAccountTransactionResponseList(transactions,accountId);
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
        
        transaction.setStatus(TransactionStatus.SUCCESS);
        TransactionModel savedTransaction = transactionRepo.save(transaction);
        return TransactionMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse cancelTransaction(UUID transactionId) {
        TransactionModel transaction = transactionRepo.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
        
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
            ResponseEntity<HashMap<String, Object>> executeTransfer = webClientAccounts.put().uri("/transfer")
                    .bodyValue(new HashMap<String, Object>() {
                        {
                            put("fromAccountId", transaction.getFromAccountId());
                            put("toAccountId", transaction.getToAccountId());
                            put("amount", transaction.getAmount());
                        }
                    })
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                             clientResponse -> clientResponse.bodyToMono(HashMap.class)
                                     .map(errorResponse -> {
                                         // Parse the actual ErrorResponse structure from account service
                                         String errorMessage = "Transfer failed";
                                         if (errorResponse != null && errorResponse.containsKey("message")) {
                                             errorMessage = (String) errorResponse.get("message");
                                         }
                                         
                                         // Map specific account service errors to meaningful transaction errors
                                         if (errorMessage.contains("Insufficient funds")) {
                                             return new TransactionExecutionException("Insufficient funds in source account");
                                         } else if (errorMessage.contains("not found")) {
                                             return new TransactionExecutionException("One or more accounts not found");
                                         } else if (errorMessage.contains("Cannot transfer to the same account")) {
                                             return new TransactionExecutionException("Cannot transfer to the same account");
                                         } else if (errorMessage.contains("Transfer amount must be positive")) {
                                             return new TransactionExecutionException("Transfer amount must be positive");
                                         } else {
                                             return new TransactionExecutionException("Transfer failed: " + errorMessage);
                                         }
                                     }))
                    .toEntity(new ParameterizedTypeReference<HashMap<String, Object>>() {})
                    .block();

            if (executeTransfer == null) {
                // Cancel transaction and throw exception
                cancelTransaction(transaction.getId());
                throw new TransactionExecutionException("Transfer failed - no response from account service");
            }
            
            // Check if response contains success message
            HashMap<String, Object> responseBody = executeTransfer.getBody();
            if (responseBody != null && responseBody.containsKey("message")) {
                String message = (String) responseBody.get("message");
                if (!message.contains("successful") && !message.contains("Account updated successfully")) {
                    // Cancel transaction if the response doesn't indicate success
                    cancelTransaction(transaction.getId());
                    throw new TransactionExecutionException("Transfer failed: " + message);
                }
            }
            
        } catch (TransactionExecutionException e) {
            // Cancel transaction and re-throw
            cancelTransaction(transaction.getId());
            throw e;
        } catch (Exception e) {
            // Cancel transaction and re-throw
            cancelTransaction(transaction.getId());
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("400")) {
                throw new TransactionExecutionException("Transfer failed due to invalid transfer data or account issues");
            } else if (errorMessage != null && errorMessage.contains("404")) {
                throw new TransactionExecutionException("Transfer failed - account not found");
            } else {
                throw new TransactionExecutionException("Transfer execution failed: " + (errorMessage != null ? errorMessage : "Unknown error occurred"));
            }
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
