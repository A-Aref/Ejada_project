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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Account management API endpoints")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/")
    @Operation(summary = "Create a new account", 
               description = "Creates a new bank account for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Account created successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "accountId": "123e4567-e89b-12d3-a456-426614174000",
                                       "accountNumber": "ACC123456789",
                                       "message": "Account created successfully"
                                   }
                                   """))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid account data",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Invalid account data"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> createAccount(
            @Parameter(description = "Account creation data", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(mediaType = "application/json",
                          examples = @ExampleObject(value = """
                              {
                                  "userId": "123e4567-e89b-12d3-a456-426614174000",
                                  "accountType": "SAVINGS",
                                  "initialBalance": 1000.00
                              }
                              """)))
            @RequestBody HashMap<String, Object> accountData) {
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
    @Operation(summary = "Get account by ID", 
               description = "Retrieves account details by account ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Account retrieved successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "accountId": "123e4567-e89b-12d3-a456-426614174000",
                                       "accountNumber": "ACC123456789",
                                       "balance": 1500.00,
                                       "accountType": "SAVINGS",
                                       "status": "ACTIVE"
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "Account not found",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Account not found"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> getAccount(
            @Parameter(description = "Account ID to retrieve", required = true)
            @PathVariable String accountId) {
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
    @Operation(summary = "Get all accounts for a user", 
               description = "Retrieves all accounts belonging to a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Accounts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "accounts": [
                                           {
                                               "accountId": "123e4567-e89b-12d3-a456-426614174000",
                                               "accountNumber": "ACC123456789",
                                               "balance": 1500.00,
                                               "accountType": "SAVINGS",
                                               "status": "ACTIVE"
                                           }
                                       ]
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "No accounts found for this user",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "No accounts found for this user"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> getAllAccounts(
            @Parameter(description = "User ID to retrieve accounts for", required = true)
            @PathVariable String userId) {
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
    @Operation(summary = "Transfer amount between accounts", 
               description = "Transfers money from one account to another")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Transfer successful",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Transfer successful"
                                   }
                                   """))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid transfer data or insufficient funds",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Insufficient balance"
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "Account not found",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Account not found."
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> transferAmount(
            @Parameter(description = "Transfer data", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(mediaType = "application/json",
                          examples = @ExampleObject(value = """
                              {
                                  "fromAccountId": "123e4567-e89b-12d3-a456-426614174000",
                                  "toAccountId": "987fcdeb-51a2-43d7-8f9e-123456789abc",
                                  "amount": 500.00
                              }
                              """)))
            @RequestBody HashMap<String, Object> transferData) {
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
    @Operation(summary = "Global exception handler", 
               description = "Handles unexpected errors in the accounts API")
    @ApiResponse(responseCode = "500", 
                description = "Internal server error",
                content = @Content(mediaType = "application/json",
                          examples = @ExampleObject(value = """
                              {
                                  "message": "An unexpected error occurred: Error details"
                              }
                              """)))
    public ResponseEntity<HashMap<String, Object>> handleException(Exception e) {
        HashMap<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
        kafkaProducerService.sendMessage(errorResponse, "Response");
        return ResponseEntity.status(500).body(errorResponse);
    }
}
