package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.PositionsDTO;

import java.util.List;

public interface PositionsService {
    public List<PositionsDTO> getPositions();
}
