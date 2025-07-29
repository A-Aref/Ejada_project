package com.ejada.bff.controller;

import com.ejada.bff.dto.DashboardResponse;
import com.ejada.bff.service.BffService;
import com.ejada.bff.service.KafkaProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<DashboardResponse> getUserDashboard(@PathVariable UUID userId) {
        kafkaProducerService.sendMessage(Map.of("userId",userId), "Request");
        DashboardResponse dashboard = bffService.getDashboard(userId);
        kafkaProducerService.sendMessage(Map.of("response",dashboard), "Response");
        return ResponseEntity.ok(dashboard);
    }
}
