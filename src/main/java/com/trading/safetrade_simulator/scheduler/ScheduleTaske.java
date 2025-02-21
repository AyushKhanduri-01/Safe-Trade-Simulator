package com.trading.safetrade_simulator.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ScheduleTaske {

    @Scheduled(cron = "0 1 0 * * ?", zone = "Asia/Kolkata")
    public void updateDaily(){

    }

//    @Scheduled(fixedRate = 1000)
//    public void updateSec(){
//
//        System.out.println("updating ");
//    }

}
