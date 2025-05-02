package com.trading.safetrade_simulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.QuotesData;

import java.util.List;
import java.util.Map;

public interface IIFLService {
    public boolean isSessionTokenPresent();
    public boolean isInstrumentPresent();
    public void addSessionToken() throws JsonProcessingException;
    public void addInstruments() throws JsonProcessingException;
    public String getSessionToken();

    Map<Integer, QuotesData> getQuoteData(List<Instruments> list);

    public Double getSingleQuoteData(String instrumentDescription);
}
