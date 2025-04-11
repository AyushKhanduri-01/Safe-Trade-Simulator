package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.BuyOrderForm;
import com.trading.safetrade_simulator.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/buy")
    public ResponseEntity<?> placeBuyOrder(@RequestBody BuyOrderForm orderForm){
//        System.out.println(orderForm);
        if(orderService.saveOrder(orderForm)){
            return ResponseEntity.ok("Order Placed");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Failed to complere Order");
        }
    }

//    @PostMapping("/exit")
//    public ResponseEntity<?> exitPosition(){
//
//    }


}
