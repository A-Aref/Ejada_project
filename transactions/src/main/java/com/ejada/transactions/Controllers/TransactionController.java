package com.ejada.transactions.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.transactions.Services.KafkaProducerService;
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
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private WebClient webClientAccounts;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<HashMap<String, Object>> getTransactions(@PathVariable String accountId) {
        // TODO: needs to add body
        kafkaProducerService.sendMessage(null, "Request");
        List<HashMap<String, Object>> transactions = transactionService.getTransactions(UUID.fromString(accountId));
        if (transactions.isEmpty()) {
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("message", "No transactions found for this account");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(404).body(response);
        }

        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("transactions", transactions);

        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/transfer/initiation")
    public ResponseEntity<HashMap<String, Object>> initiateTransaction(@RequestBody HashMap<String, Object> body) {
        kafkaProducerService.sendMessage(body, "Request");
        ResponseEntity<Object> fromAccount = webClientAccounts.get()
                .uri("/{accountId}", body.get("fromAccountId"))
                .retrieve().toEntity(Object.class).block();
        ResponseEntity<Object> toAccount = webClientAccounts.get()
                .uri("/{accountId}", body.get("toAccountId"))
                .retrieve().toEntity(Object.class).block();
        if (fromAccount.getStatusCode().isError() || toAccount.getStatusCode().isError()) {

            HashMap<String, Object> errorResponse = new HashMap<String, Object>();
            errorResponse.put("message", "Invalid accounts");
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(400).body(errorResponse);
        }
        TransactionModel transaction = transactionService.initiateTransaction(body);
        if (transaction == null) {
            HashMap<String, Object> errorResponse = new HashMap<String, Object>();
            errorResponse.put("message", "Invalid amount");
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(400).body(errorResponse);
        }

        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("transactionId", transaction.getId());
        response.put("status", transaction.getStatus().getString());
        response.put("timestamp", transaction.getCreatedAt().toString());
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<HashMap<String, Object>> executeTransaction(@RequestBody HashMap<String, Object> body) {
        kafkaProducerService.sendMessage(body, "Request");
        TransactionModel transaction = transactionService
                .getTransaction(UUID.fromString(body.get("transactionId").toString()));
        if (transaction == null || transaction.getStatus() == TransactionStatus.SUCCESS) {
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("message", "Invalid transaction or already executed");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(400).body(response);
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
            String message = hash != null ? "Transfer failed due" + (String) hash.get("message") : "Transfer failed";
            HashMap<String, Object> errorResponse = new HashMap<String, Object>();
            errorResponse.put("message", message);
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(400).body(errorResponse);
        } else {
            transactionService.excuteTransaction(transaction.getId());
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("transactionId", transaction.getId());
            response.put("status", transaction.getStatus().getString());
            response.put("timestamp", transaction.getCreatedAt().toString());
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(200).body(response);
        }
    }

    @GetMapping("/accounts/{accountId}/getLatest")
    public ResponseEntity<HashMap<String, Object>> getLatestTransaction(@PathVariable String accountId) {
        // TODO: needs to add body
        kafkaProducerService.sendMessage(null, "Request");
        HashMap<String, Object> transaction = transactionService.getLatestTransaction(UUID.fromString(accountId));
        if (transaction == null) {
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("message", "No transactions found for this account");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(404).body(response);
        }
        kafkaProducerService.sendMessage(transaction, "Response");
        return ResponseEntity.status(200).body(transaction);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, Object>> handleException(Exception e) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
        kafkaProducerService.sendMessage(errorResponse, "Response");
        return ResponseEntity.status(500).body(errorResponse);
    }
}
