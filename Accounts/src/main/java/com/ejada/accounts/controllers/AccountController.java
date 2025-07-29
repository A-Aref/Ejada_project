package com.ejada.accounts.Controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejada.accounts.Services.AccountService;
import com.ejada.accounts.Services.KafkaProducerService;
import com.ejada.accounts.dto.CreateAccountRequest;
import com.ejada.accounts.dto.AccountResponse;
import com.ejada.accounts.dto.AccountListResponse;
import com.ejada.accounts.dto.TransferRequest;
import com.ejada.accounts.dto.TransferResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {

        kafkaProducerService.sendMessage(request, "Request");

        AccountResponse response = accountService.createAccount(request);

        kafkaProducerService.sendMessage(response, "Response");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId) {
        kafkaProducerService.sendMessage(Map.of("accountId", accountId), "Request");
        
        AccountResponse response = accountService.getAccount(UUID.fromString(accountId));
        
        kafkaProducerService.sendMessage(response, "Response");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<AccountListResponse> getAllAccounts(@PathVariable String userId) {

        kafkaProducerService.sendMessage(Map.of("userId", userId), "Request");
        
        AccountListResponse response = accountService.getAllAccounts(UUID.fromString(userId));
        
        kafkaProducerService.sendMessage(response, "Response");
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/transfer")
    public ResponseEntity<TransferResponse> transferAmount(@Valid @RequestBody TransferRequest request) {

        kafkaProducerService.sendMessage(request, "Request");
        
        TransferResponse response = accountService.transferAmount(request);

        kafkaProducerService.sendMessage(response, "Response");

        return ResponseEntity.ok(response);
    }
}
