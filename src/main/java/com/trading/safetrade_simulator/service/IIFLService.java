package com.trading.safetrade_simulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IIFLService {
    public boolean isSessionTokenPresent();
    public boolean isInstrumentPresent();
    public void addSessionToken() throws JsonProcessingException;
    public void addInstruments() throws JsonProcessingException;
    public String getSessionToken();
}
