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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Transaction management API endpoints")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private WebClient webClientAccounts;

    @GetMapping("/accounts/{accountId}")
    @Operation(summary = "Get transactions by account ID", 
               description = "Retrieves all transactions for a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Successfully retrieved transactions",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "transactions": [
                                           {
                                               "id": "123e4567-e89b-12d3-a456-426614174000",
                                               "amount": 100.00,
                                               "status": "SUCCESS",
                                               "timestamp": "2024-01-01T10:00:00"
                                           }
                                       ]
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "No transactions found for this account",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "No transactions found for this account"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> getTransactions(
            @Parameter(description = "Account ID to retrieve transactions for", required = true)
            @PathVariable String accountId) {
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
    @Operation(summary = "Initiate a transfer transaction", 
               description = "Creates a new transfer transaction between two accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Transaction initiated successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "transactionId": "123e4567-e89b-12d3-a456-426614174000",
                                       "status": "PENDING",
                                       "timestamp": "2024-01-01T10:00:00"
                                   }
                                   """))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid request - invalid accounts or amount",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Invalid accounts"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> initiateTransaction(
            @Parameter(description = "Transaction initiation request", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(mediaType = "application/json",
                          examples = @ExampleObject(value = """
                              {
                                  "fromAccountId": "123e4567-e89b-12d3-a456-426614174000",
                                  "toAccountId": "987fcdeb-51a2-43d7-8f9e-123456789abc",
                                  "amount": 100.00
                              }
                              """)))
            @RequestBody HashMap<String, Object> body) {
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
    @Operation(summary = "Execute a transfer transaction", 
               description = "Executes a previously initiated transfer transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Transaction executed successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "transactionId": "123e4567-e89b-12d3-a456-426614174000",
                                       "status": "SUCCESS",
                                       "timestamp": "2024-01-01T10:00:00"
                                   }
                                   """))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid transaction or already executed",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Invalid transaction or already executed"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> executeTransaction(
            @Parameter(description = "Transaction execution request", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(mediaType = "application/json",
                          examples = @ExampleObject(value = """
                              {
                                  "transactionId": "123e4567-e89b-12d3-a456-426614174000"
                              }
                              """)))
            @RequestBody HashMap<String, Object> body) {
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
    @Operation(summary = "Get latest transaction for account", 
               description = "Retrieves the most recent transaction for a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Successfully retrieved latest transaction",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "id": "123e4567-e89b-12d3-a456-426614174000",
                                       "amount": 100.00,
                                       "status": "SUCCESS",
                                       "timestamp": "2024-01-01T10:00:00"
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "No transactions found for this account",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "No transactions found for this account"
                                   }
                                   """)))
    })
    public ResponseEntity<HashMap<String, Object>> getLatestTransaction(
            @Parameter(description = "Account ID to retrieve latest transaction for", required = true)
            @PathVariable String accountId) {
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
    @Operation(summary = "Global exception handler", 
               description = "Handles unexpected errors in the transaction API")
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
