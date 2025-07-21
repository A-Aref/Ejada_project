package com.ejada.accounts.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejada.accounts.Services.AccountService;
import com.ejada.accounts.Services.KafkaProducerService;
import com.ejada.accounts.Models.AccountModel;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/")
    public ResponseEntity<HashMap<String, Object>> createAccount(@RequestBody HashMap<String, Object> accountData) {
        kafkaProducerService.sendMessage(accountData, "Request");
        AccountModel account = accountService.createAccount(accountData);
        if (account == null) {
            // Log the error message
            HashMap<String, Object> response = new HashMap<String, Object>() {
                {
                    put("message", "Invalid account data");
                }
            };
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(400).body(response);
        }
        HashMap<String, Object> response = new HashMap<String, Object>() {
            {
                put("accountId", account.getId());
                put("accountNumber", account.getAccountNumber());
                put("message", "Account created successfully");
            }
        };
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<HashMap<String, Object>> getAccount(@PathVariable String accountId) {
        // TODO: needs to add body
        kafkaProducerService.sendMessage(null, "Request");
        AccountModel account = accountService.getAccount(UUID.fromString(accountId));
        if (account == null) {
            HashMap<String, Object> response = new HashMap<String, Object>() {
                {
                    put("message", "Account not found");
                }
            };
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(404).body(response);
        }
        HashMap<String, Object> response = new HashMap<String, Object>() {
            {
                put("accountId", account.getId());
                put("accountNumber", account.getAccountNumber());
                put("balance", account.getBalance());
                put("accountType", account.getAccountType().getString());
                put("status", account.getStatus().getString());
            }
        };
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<HashMap<String, Object>> getAllAccounts(@PathVariable String userId) {
        // TODO: needs to add body
        kafkaProducerService.sendMessage(null, "Request");
        List<AccountModel> accounts = accountService.getAllAccounts(UUID.fromString(userId));
        if (accounts.isEmpty()) {
            HashMap<String, Object> response = new HashMap<String, Object>() {
                {
                    put("message", "No accounts found for this user");
                }
            };
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(404).body(response);
        }

        List<HashMap<String, Object>> accountList = accounts.stream().map(account -> {
            HashMap<String, Object> accountData = new HashMap<>();
            accountData.put("accountId", account.getId());
            accountData.put("accountNumber", account.getAccountNumber());
            accountData.put("balance", account.getBalance());
            accountData.put("accountType", account.getAccountType().getString());
            accountData.put("status", account.getStatus().getString());
            return accountData;
        }).toList();
        HashMap<String, Object> response = new HashMap<>() {
            {
                put("accounts", accountList);
            }
        };
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/transfer")
    public ResponseEntity<HashMap<String, Object>> transferAmount(@RequestBody HashMap<String, Object> transferData) {
        kafkaProducerService.sendMessage(transferData, "Request");
        try {
            accountService.updateAmount(UUID.fromString((String) transferData.get("fromAccountId")),
                    UUID.fromString((String) transferData.get("toAccountId")),
                    (Double) transferData.get("amount"));
            HashMap<String, Object> response = new HashMap<String, Object>();
            response.put("message", "Transfer successful");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(200).body(response);
        } catch (IllegalArgumentException e) {

            HashMap<String, Object> errorResponse = new HashMap<String, Object>();
            errorResponse.put("message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");

            HttpStatusCode statusCode;
            if ("Account not found.".equals(e.getMessage())) {
                statusCode = HttpStatusCode.valueOf(404);
            } else {
                statusCode = HttpStatusCode.valueOf(400);
            }
            return ResponseEntity.status(statusCode).body(errorResponse);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, Object>> handleException(Exception e) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
        kafkaProducerService.sendMessage(errorResponse, "Response");
        return ResponseEntity.status(500).body(errorResponse);
    }
}
