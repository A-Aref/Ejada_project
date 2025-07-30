package com.ejada.bff.service;

import com.ejada.bff.dto.DashboardResponse;

import java.util.UUID;

public interface BffService {
    public DashboardResponse getDashboard(UUID userId);
}
