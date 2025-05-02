package com.trading.safetrade_simulator.DTO;

import lombok.Data;

@Data
public class PositionsDTO {
    private String positionId;
    private String productType;
    private String orderDuration;
    private String instrumentDescription;
    private int quantity;
    private double avgPrice;
    private double LTP;
    private double PandL;
    private String change;
    private String type;
    private double stopLoss;
    private double targetPrice;

    private double exitPrice;
}
