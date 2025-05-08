package com.trading.safetrade_simulator.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading.safetrade_simulator.service.IIFLService;
import com.trading.safetrade_simulator.service.OrderService;
import com.trading.safetrade_simulator.service.PositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ScheduleTaske {
    @Autowired
    private IIFLService iiflService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PositionsService positionsService;

    @Scheduled(cron = "0 1 0 * * ?", zone = "Asia/Kolkata")
    public void updateDaily() throws JsonProcessingException {
        if (!iiflService.isSessionTokenPresent()) {
            System.out.println("session not present");
            iiflService.addSessionToken();
            System.out.println("session token added");
        }

        if (!iiflService.isInstrumentPresent()) {
            System.out.println("Instrument not present");
            iiflService.addInstruments();
            System.out.println("Instrument added");
        }
    }

    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Kolkata")
    public void squareOFFPosition(){
      positionsService.executeSquareOff();
    }
    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Kolkata")
    public void cancelPendigOrder(){
        orderService.cancelPendingOrder();
    }

//    @Scheduled(fixedRate = 3000)
//    public void executeLimitOrder(){
//        orderService.executeLimitOrder();
//    }

//    @Scheduled(fixedRate = 3000)
//    public void executestopLossandTarget(){
//        positionsService.executeStopLandTarget();
//    }

}
