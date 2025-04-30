package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.BuyOrderForm;
import com.trading.safetrade_simulator.model.OrderDetails;
import com.trading.safetrade_simulator.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.security.cert.CollectionCertStoreParameters;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(){
        List<OrderDetails> orders = orderService.getAllOrders();
        Collections.reverse(orders);
        return  new ResponseEntity<>(orders,HttpStatus.OK);
    }

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
