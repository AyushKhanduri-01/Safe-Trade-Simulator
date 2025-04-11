package com.trading.safetrade_simulator.model;

import com.trading.safetrade_simulator.enums.OrderStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class OrderDetails {
    @Id
    private String id; // id for each order
    private String instrumentDescription; // key description
    private int quantity; // total quantity (lot Size * quantity)
    private double price; //At what price order placed
    private double totalAmount; // Total order amount
    private LocalDateTime createdAt;  // time when order created
    private LocalDateTime updatedAt;  //time when order updated
    private String orderType; //buy or sell
    private String orderDuration;// Intraday
    private String productType; //limit/Market
    private double limitPrice;
    private double stopLoss = -1.00;
    private boolean stopLossTrigger=false;
    private double target = -1.00;
    private boolean targetTrigger=false;
    private OrderStatus orderStatus;  //pending/placed/canceled/rejected
//    private CancelRejectReason cancelRejectReason; // cancel(Cancelled by user)/ reject (Insufficient Amount)

    @DBRef
    private User user;
}
