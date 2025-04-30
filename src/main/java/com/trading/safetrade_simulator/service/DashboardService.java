package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.DashboardData;
import org.springframework.security.core.Authentication;

public interface DashboardService {
    public DashboardData getDashboardData(Authentication authentication);
}
