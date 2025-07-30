package com.ejada.transactions.Controllers;

import java.util.Map;
import java.util.UUID;

import com.ejada.transactions.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.transactions.Services.KafkaProducerService;
import com.ejada.transactions.Services.TransactionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountTransactions> getTransactions(@PathVariable String accountId) {

        kafkaProducerService.sendMessage(Map.of("accountId", accountId), "Request");

        AccountTransactions transactions = transactionService.getTransactions(UUID.fromString(accountId));
        
        kafkaProducerService.sendMessage(transactions, "Response");
        
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer/initiation")
    public ResponseEntity<TransactionResponse> initiateTransaction(@Valid @RequestBody TransactionRequest request) {

        kafkaProducerService.sendMessage(request, "Request");
        
        TransactionResponse response = transactionService.initiateTransactionWithValidation(request);

        kafkaProducerService.sendMessage(response, "Response");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<TransactionResponse> executeTransaction(@Valid @RequestBody TransactionExecutionRequest request) {

        kafkaProducerService.sendMessage(request, "Request");
        
        TransactionResponse response = transactionService.executeTransfer(request);

        kafkaProducerService.sendMessage(response, "Response");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{accountId}/getLatest")
    public ResponseEntity<TransactionResponse> getLatestTransaction(@PathVariable String accountId) {

        kafkaProducerService.sendMessage(Map.of("accountId", accountId), "Request");
        
        TransactionResponse transaction = transactionService.getLatestTransaction(UUID.fromString(accountId));

        kafkaProducerService.sendMessage(transaction, "Response");

        return ResponseEntity.ok(transaction);
    }
    
}
