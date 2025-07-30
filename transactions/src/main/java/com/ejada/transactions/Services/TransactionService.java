package com.ejada.transactions.Services;

import java.util.UUID;

import com.ejada.transactions.dto.*;
import com.ejada.transactions.Models.TransactionModel;

public interface TransactionService {
    
    TransactionResponse getTransaction(UUID transactionId);
    
    TransactionResponse getLatestTransaction(UUID accountId);

    AccountTransactions getTransactions(UUID accountId);
    
    TransactionResponse initiateTransaction(TransactionRequest request);
    
    TransactionResponse initiateTransactionWithValidation(TransactionRequest request);
    
    TransactionResponse executeTransaction(UUID transactionId);
    
    TransactionResponse executeTransfer(TransactionExecutionRequest request);
    
    TransactionResponse cancelTransaction(UUID transactionId);
    
    TransactionModel getTransactionModel(UUID transactionId);
}


