package com.ejada.bff.controller;

import com.ejada.bff.dto.DashboardResponse;
import com.ejada.bff.service.impl.BffServiceImpl;
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

    private final BffServiceImpl bffServiceImpl;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public BffController(BffServiceImpl bffServiceImpl) {
        this.bffServiceImpl = bffServiceImpl;
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardResponse> getUserDashboard(@PathVariable UUID userId) {
        kafkaProducerService.sendMessage(Map.of("userId",userId), "Request");
        DashboardResponse dashboard = bffServiceImpl.getDashboard(userId);
        kafkaProducerService.sendMessage(Map.of("response",dashboard), "Response");
        return ResponseEntity.ok(dashboard);
    }
}
