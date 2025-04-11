package com.trading.safetrade_simulator.DTO;

import lombok.Data;

@Data
public class BuyOrderForm {
    private String description; //BANKNIFTY25APR44300CE  but display "Display Name" of Instrument
    private String orderDuration; // Intraday
    private int lotSize; // (lot size)
    private int quantity; // (How many lots) -> Input
    private double price; // (Current traded price)
    private String productType; // Market Order or Limit order -> Input radio button
    private Double limitPrice; // At what price should buy   -> Input
    private double amount; // Total amount of order
    private Double stopLoss = -1.0; // if user want -> Input
    private Double targetPrice = -1.0; // if user want -> Input
    private String orderType; // BUY -> Button (form submit)

}



