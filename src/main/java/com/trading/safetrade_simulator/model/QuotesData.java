package com.trading.safetrade_simulator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuotesData {
    @JsonProperty("MessageCode")
    private int messageCode;

    @JsonProperty("ExchangeSegment")
    private int exchangeSegment;

    @JsonProperty("ExchangeInstrumentID")
    private int exchangeInstrumentID;

    @JsonProperty("LastTradedPrice")
    private double lastTradedPrice;

    @JsonProperty("LastTradedQunatity")
    private int lastTradedQuantity;

    @JsonProperty("TotalBuyQuantity")
    private int totalBuyQuantity;

    @JsonProperty("TotalSellQuantity")
    private int totalSellQuantity;

    @JsonProperty("TotalTradedQuantity")
    private int totalTradedQuantity;

    @JsonProperty("AverageTradedPrice")
    private double averageTradedPrice;

    @JsonProperty("LastTradedTime")
    private long lastTradedTime;

    @JsonProperty("LastUpdateTime")
    private long lastUpdateTime;

    @JsonProperty("PercentChange")
    private double percentChange;

    @JsonProperty("Open")
    private double open;

    @JsonProperty("High")
    private double high;

    @JsonProperty("Low")
    private double low;

    @JsonProperty("Close")
    private double close;

    @JsonProperty("TotalValueTraded")
    private Double totalValueTraded;

    @JsonProperty("AskInfo")
    private AskInfo askInfo;

    @JsonProperty("BidInfo")
    private BidInfo bidInfo;

    @JsonProperty("XMarketType")
    private int xMarketType;

    @JsonProperty("BookType")
    private int bookType;

    @Data
    public static class AskInfo {

        @JsonProperty("Size")
        private int size;

        @JsonProperty("Price")
        private double price;

        @JsonProperty("TotalOrders")
        private int totalOrders;

        @JsonProperty("BuyBackMarketMaker")
        private int buyBackMarketMaker;
    }

    @Data
    public static class BidInfo {

        @JsonProperty("Size")
        private int size;

        @JsonProperty("Price")
        private double price;

        @JsonProperty("TotalOrders")
        private int totalOrders;

        @JsonProperty("BuyBackMarketMaker")
        private int buyBackMarketMaker;
    }
}
