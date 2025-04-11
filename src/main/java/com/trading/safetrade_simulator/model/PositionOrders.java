package com.trading.safetrade_simulator.model;

import com.trading.safetrade_simulator.enums.OrderStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class PositionOrders {
    @Id
    private String id; // id for each order
    private String instrumentDescription; // key description
    private int quantity; // current quantity (lot Size * quantity)
//    private int totalQuantity;
    private double price; // At what price position created
    private double exitPrice; // At what price position exit
    private double PAndL = 0.00;
    private double percentChange;// Total profit and loss in trade
    private double totalAmount; // Total order amount
    private LocalDateTime createdAt; // time when order created
    private LocalDateTime updatedAt; // time when order updated
    private String orderType; // buy or sell
    private String orderDuration;// Intraday
    private String productType; // limit/Market
    private double stopLoss = -1.00;
    private boolean stopLossTrigger = false;
    private double target = -1.00;
    private boolean targetTrigger = false;
    private OrderStatus orderStatus; // pending/placed/canceled/rejected
//    private CancelRejectReason cancelRejectReason;

    @DBRef
    private User user;
}
