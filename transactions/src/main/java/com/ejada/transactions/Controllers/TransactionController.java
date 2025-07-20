package com.ejada.transactions.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.transactions.Services.TransactionService;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ejada.transactions.Models.TransactionModel;
import com.ejada.transactions.Models.TransactionStatus;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WebClient webClientAccounts;


    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<HashMap<String, Object>> getTransactions(@PathVariable String accountId) {
        List<HashMap<String, Object>> transactions = transactionService.getTransactions(UUID.fromString(accountId));
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "No transactions found for this account");
                        }
                    });
        }
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("transactions", transactions);
                    }
                });
    }

    @PostMapping("/transfer/initiation")
    public ResponseEntity<HashMap<String, Object>> initiateTransaction(@RequestBody HashMap<String, Object> body) {
        ResponseEntity<Object> fromAccount = webClientAccounts.get()
                .uri("/{accountId}", body.get("fromAccountId"))
                .retrieve().toEntity(Object.class).block();
        ResponseEntity<Object> toAccount = webClientAccounts.get()
                .uri("/{accountId}", body.get("toAccountId"))
                .retrieve().toEntity(Object.class).block();
        if (fromAccount.getStatusCode().isError() || toAccount.getStatusCode().isError()) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid account");
                        }
                    });
        }
        TransactionModel transaction = transactionService.initiateTransaction(body);
        if (transaction == null) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid amount");
                        }
                    });
        }
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("transactionId", transaction.getId());
                        put("status", transaction.getStatus().getString());
                        put("timestamp", transaction.getCreatedAt().toString());
                    }
                });
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<HashMap<String, Object>> executeTransaction(@RequestBody HashMap<String, Object> body) {
        TransactionModel transaction = transactionService
                .getTransaction(UUID.fromString(body.get("transactionId").toString()));
        if (transaction == null || transaction.getStatus() == TransactionStatus.SUCCESS) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid transaction or already executed");
                        }
                    });
        }
        ResponseEntity<Object> excuteTransfer = webClientAccounts.put().uri("/transfer")
                .bodyValue(new HashMap<String, Object>() {
                    {
                        put("fromAccountId", transaction.getFromAccountId());
                        put("toAccountId", transaction.getToAccountId());
                        put("amount", transaction.getAmount());
                    }
                })
                .retrieve().toEntity(Object.class).block();

        if (excuteTransfer.getStatusCode().isError()) {
            transactionService.cancelTransaction(transaction.getId());
            @SuppressWarnings("unchecked")
            HashMap<String, Object> hash = (HashMap<String, Object>) excuteTransfer.getBody();
            String message = hash != null ? (String) hash.get("message") : "Transfer failed";
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Transfer failed due" + message);
                        }
                    });
        } else {
            transactionService.excuteTransaction(transaction.getId());
            return ResponseEntity.status(200).body(
                    new HashMap<String, Object>() {
                        {
                            put("transactionId", transaction.getId());
                            put("status", transaction.getStatus().getString());
                            put("timestamp", transaction.getCreatedAt().toString());
                        }
                    });
        }
    }

    @GetMapping("/accounts/{accountId}/getLatest")
    public ResponseEntity<HashMap<String, Object>> getLatestTransaction(@PathVariable String accountId) {
        HashMap<String,Object> transaction = transactionService.getLatestTransaction(UUID.fromString(accountId));
        if (transaction == null) {
            return ResponseEntity.status(404).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "No transactions found for this account");
                        }
                    });
        }
        return ResponseEntity.status(200).body(transaction);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(500).body(
                new HashMap<String, Object>() {
                    {
                        put("message", "An unexpected error occurred: " + e.getMessage());
                    }
                });
    }
}
