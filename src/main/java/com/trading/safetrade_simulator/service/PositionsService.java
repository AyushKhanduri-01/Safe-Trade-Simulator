package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.PositionsDTO;
import com.trading.safetrade_simulator.model.PositionOrders;

import java.time.LocalDate;
import java.util.List;

public interface PositionsService {
    public List<PositionsDTO> getPositions();

    public void executeStopLandTarget();

    void executeSquareOff();

    List<PositionOrders> getReport(LocalDate startDate, LocalDate endDate);

    void editPosition(String positionId, Double stopLoss, Double target);

    void exitPosition(String positionId,Double ltp);
}
