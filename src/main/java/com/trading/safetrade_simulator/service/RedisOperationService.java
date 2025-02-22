package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.model.Instruments;

import java.time.Instant;
import java.util.List;

public interface RedisOperationService {
    public <T> T findByKey(String key, Class<T> type);
    public Instant expirationTime();
    public boolean isSessionTokenPresent(String key);
    public boolean isInstrumentPresent(String key);

    public void saveSessionToken(String key,String token, Instant expiryTime);
    public void saveInstrumentToken(String key,String token, Instant expiryTime);

    public void saveInstrumentInCache(Instruments instruments,String key,Instant exityTime);

    List<Instruments> findByPattern(String query);
}
