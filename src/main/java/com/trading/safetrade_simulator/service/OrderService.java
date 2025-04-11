package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.BuyOrderForm;

public interface OrderService {
    boolean saveOrder(BuyOrderForm orderForm);
}
