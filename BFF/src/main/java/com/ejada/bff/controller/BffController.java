package com.ejada.bff.controller;

import com.ejada.bff.dto.DashboardResponse;
import com.ejada.bff.exception.NotFoundException;
import com.ejada.bff.service.BffService;
import com.ejada.bff.service.KafkaProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bff")
public class BffController {

    private final BffService bffService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public BffController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getUserDashboard(@PathVariable UUID userId) {
        //TODO: Add body to the request

        kafkaProducerService.sendMessage(null, "Request");
        try{
            DashboardResponse dashboard = bffService.getDashboard(userId);
            kafkaProducerService.sendMessage(Map.of("response",dashboard), "Response");
            return ResponseEntity.ok(dashboard);
        }
        catch(NotFoundException e){
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
