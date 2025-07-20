package com.ejada.accounts.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejada.accounts.Services.AccountService;
import com.ejada.accounts.Models.AccountModel;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/")
    public ResponseEntity<HashMap<String, Object>> createAccount(@RequestBody HashMap<String, Object> accountData) {
        AccountModel account = accountService.createAccount(accountData);
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
                        put("accountNumber", account.getAccountNumber());
                        put("message", "Account created successfully");
                    }
                });
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<HashMap<String, Object>> getAccount(@PathVariable String accountId) {
        AccountModel account = accountService.getAccount(UUID.fromString(accountId));
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
                        put("accountNumber", account.getAccountNumber());
                        put("balance", account.getBalance());
                        put("accountType", account.getAccountType().getString());
                        put("status", account.getStatus().getString());
                    }
                });
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<HashMap<String, Object>> getAllAccounts(@PathVariable String userId) {
        List<AccountModel> accounts = accountService.getAllAccounts(UUID.fromString(userId));
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
            accountData.put("accountNumber", account.getAccountNumber());
            accountData.put("balance", account.getBalance());
            accountData.put("accountType", account.getAccountType().getString());
            accountData.put("status", account.getStatus().getString());
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
            accountService.updateAmount(UUID.fromString((String) transferData.get("fromAccountId")),
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
