package com.trading.safetrade_simulator.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading.safetrade_simulator.service.IIFLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ScheduleTaske {
    @Autowired
    private IIFLService iiflService;
    @Scheduled(cron = "0 1 0 * * ?", zone = "Asia/Kolkata")
    public void updateDaily() throws JsonProcessingException {
        if (!iiflService.isSessionTokenPresent()) {
            iiflService.addSessionToken();
        }

        if (!iiflService.isInstrumentPresent()) {
            iiflService.addInstruments();
        }
    }

//    @Scheduled(fixedRate = 1000)
//    public void updateSec(){
//
//        System.out.println("updating ");
//    }

}
