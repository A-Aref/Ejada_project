package com.ejada.transactions.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.transactions.Services.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ejada.transactions.Models.TransactionModel;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts/{accountId}/transactions")
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

    @PostMapping("/transactions/transfer/initiation")
    public ResponseEntity<HashMap<String, Object>> initiateTransaction(HashMap<String, Object> body) {
        TransactionModel transaction = transactionService.initiateTransaction(body);
        if (transaction == null) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid account or insufficient funds");
                        }
                    });
        }
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("transactionId", transaction.getId());
                        put("status", transaction.getStatus().getString());
                        put("timestamp", transaction.getCreated_at().toString());
                    }
                });
    }

    @PostMapping("/transactions/transfer/execution")
    public ResponseEntity<HashMap<String, Object>> executeTransaction(@RequestBody HashMap<String, Object> body) {
        TransactionModel transaction = transactionService
                .excuteTransaction(UUID.fromString(body.get("transactionId").toString()));
        if (transaction == null) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid account or insufficient funds");
                        }
                    });
        }
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("transactionId", transaction.getId());
                        put("status", transaction.getStatus().getString());
                        put("timestamp", transaction.getCreated_at().toString());
                    }
                });
    }

}
