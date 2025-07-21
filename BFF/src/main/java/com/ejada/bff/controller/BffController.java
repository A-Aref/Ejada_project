package com.ejada.bff.controller;

import com.ejada.bff.dto.DashboardResponse;
import com.ejada.bff.service.BffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff")
public class BffController {

    private final BffService bffService;

    public BffController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardResponse> getUserDashboard(@PathVariable UUID userId) {
        DashboardResponse dashboard = bffService.getDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }
}
