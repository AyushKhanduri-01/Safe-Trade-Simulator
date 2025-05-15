package com.trading.safetrade_simulator.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DashboardData {
    private  String name;
    private String email;
    private double remainingFund;
    private int totalOrders;
    private int totalPositions;
    private int todayOrders;
    private int todayPosition;
    private int activePosition;
}
