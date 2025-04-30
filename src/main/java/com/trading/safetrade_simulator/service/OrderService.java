package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.BuyOrderForm;
import com.trading.safetrade_simulator.model.OrderDetails;

import java.util.List;

public interface OrderService {
    boolean saveOrder(BuyOrderForm orderForm);

    List<OrderDetails> getAllOrders();
}
