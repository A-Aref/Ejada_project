package com.ejada.accounts.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejada.accounts.models.account_model;
import com.ejada.accounts.services.account_service;

@RestController
@RequestMapping("/accounts")
public class account_controller {

    @Autowired
    private account_service accountService;

    @PostMapping("/")
    public ResponseEntity<HashMap<String, Object>> createAccount(@RequestBody HashMap<String, Object> accountData) {
        account_model account = accountService.create_account(accountData);
        if (account == null) {
            return ResponseEntity.status(400).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Invalid account data");
                        }
                    });
        }
        return ResponseEntity.status(201).body(
                new HashMap<String, Object>() {
                    {
                        put("accountId", account.getId());
                        put("accountNumber", account.getAccount_number());
                        put("message", "Account created successfully");
                    }
                });
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<HashMap<String, Object>> getAccount(@PathVariable String accountId) {
        account_model account = accountService.get_account(UUID.fromString(accountId));
        if (account == null) {
            return ResponseEntity.status(404).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Account not found");
                        }
                    });
        }
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("accountId", account.getId());
                        put("accountNumber", account.getAccount_number());
                        put("balance", account.getBalance());
                        put("accountType", account.getAccount_type().getType());
                        put("status", account.getStatus().getStatus());
                    }
                });
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<HashMap<String, Object>> getAllAccounts(@PathVariable String userId) {
        List<account_model> accounts = accountService.get_all_accounts(UUID.fromString(userId));
        if (accounts.isEmpty()) {
            return ResponseEntity.status(404).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "No accounts found for this user");
                        }
                    });
        }
        List<HashMap<String, Object>> accountList = accounts.stream().map(account -> {
            HashMap<String, Object> accountData = new HashMap<>();
            accountData.put("accountId", account.getId());
            accountData.put("accountNumber", account.getAccount_number());
            accountData.put("balance", account.getBalance());
            accountData.put("accountType", account.getAccount_type().getType());
            accountData.put("status", account.getStatus().getStatus());
            return accountData;
        }).toList();
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {
                    {
                        put("accounts", accountList);
                    }
                });
    }

    @PutMapping("/transfer")
    public ResponseEntity<HashMap<String, Object>> transferAmount(@RequestBody HashMap<String, Object> transferData) {
        try {
            accountService.update_amount(UUID.fromString((String) transferData.get("fromAccountId")),
                    UUID.fromString((String) transferData.get("toAccountId")),
                    (Double) transferData.get("amount"));
            return ResponseEntity.status(200).body(
                    new HashMap<String, Object>() {
                        {
                            put("message", "Transfer successful");
                        }
                    });
        } catch (IllegalArgumentException e) {
            if ("Account not found.".equals(e.getMessage())) {
                return ResponseEntity.status(404).body(
                        new HashMap<String, Object>() {
                            {
                                put("message", e.getMessage());
                            }
                        });
            } else {
                return ResponseEntity.status(400).body(
                        new HashMap<String, Object>() {
                            {
                                put("message", e.getMessage());
                            }
                        });
            }
        }
    }

}
