package com.ejada.bff.controller;

import com.ejada.bff.dto.DashboardResponse;
import com.ejada.bff.exception.UserNotFoundException;
import com.ejada.bff.service.BffService;
import com.ejada.bff.service.KafkaProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bff")
@Tag(name = "BFF", description = "Backend for Frontend API endpoints")
public class BffController {

    private final BffService bffService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public BffController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/dashboard/{userId}")
    @Operation(summary = "Get user dashboard", 
               description = "Retrieves aggregated dashboard data for a user including accounts and recent transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Dashboard data retrieved successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "userId": "123e4567-e89b-12d3-a456-426614174000",
                                       "username": "john_doe",
                                       "accounts": [
                                           {
                                               "accountId": "acc-123",
                                               "accountNumber": "ACC123456789",
                                               "balance": 1500.00,
                                               "accountType": "SAVINGS"
                                           }
                                       ],
                                       "recentTransactions": [
                                           {
                                               "transactionId": "txn-456",
                                               "amount": 100.00,
                                               "type": "TRANSFER",
                                               "timestamp": "2024-01-01T10:00:00"
                                           }
                                       ]
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "User not found",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "User not found"
                                   }
                                   """)))
    })
    public ResponseEntity<?> getUserDashboard(
            @Parameter(description = "User ID to retrieve dashboard for", required = true)
            @PathVariable UUID userId) {
        //TODO: Add body to the request

        kafkaProducerService.sendMessage(null, "Request");
        try{
            DashboardResponse dashboard = bffService.getDashboard(userId);
            kafkaProducerService.sendMessage(Map.of("response",dashboard), "Response");
            return ResponseEntity.ok(dashboard);
        }
        catch(UserNotFoundException e){
            Map<String, Object> errorResponse = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch(Exception e){
            Map<String, Object> errorResponse = Map.of(
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }
}
