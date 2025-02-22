package com.trading.safetrade_simulator.service.impl;


import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.service.RedisOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RedisOperationServiceImpl implements RedisOperationService {

    @Autowired
    private RedisTemplate <String,Object> redisTemplate ;

    @Override
    public Instant expirationTime() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(23, 59);
        LocalDateTime datatime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = datatime.atZone(ZoneId.of("GMT"));
        return zonedDateTime.toInstant();
    }

    @Override
    public <T> T findByKey(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            if (type == String.class && value instanceof String) {
                return type.cast(value);
            }
//            else if (type == Instrument.class && value instanceof Instrument) {
//                return type.cast(value);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean isSessionTokenPresent(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        return (object != null)?true:false;
    }


    @Override
    public boolean isInstrumentPresent(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        return (object != null) ?true:false;
    }

    @Override
    public void saveSessionToken(String key, String token, Instant expiryTime) {
        try {
            redisTemplate.opsForValue().set(key, token);
            redisTemplate.expireAt(key, expiryTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveInstrumentToken(String key, String token, Instant expiryTime) {
        try {
            redisTemplate.opsForValue().set(key, token);
            redisTemplate.expireAt(key, expiryTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveInstrumentInCache(Instruments instrument, String key, Instant exityTime) {
        try{
            redisTemplate.opsForValue().set(key,instrument);
            redisTemplate.expireAt(key,exityTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Instruments> findByPattern(String pattern) {
        List<Instruments> instruments = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(pattern + "*");
            for (String key : keys) {
                if (key.startsWith(pattern) && !key.equals("IIFLSession") && !key.equals("IsInsturmentPresent")) {
                    Instruments instrument = (Instruments) redisTemplate.opsForValue().get(key);
                    instruments.add(instrument);
                    if (instruments.size() > 30)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instruments;
    }


}
