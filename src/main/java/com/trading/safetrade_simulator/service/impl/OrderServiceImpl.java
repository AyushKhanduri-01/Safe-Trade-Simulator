package com.trading.safetrade_simulator.service.impl;

import com.trading.safetrade_simulator.DTO.BuyOrderForm;
import com.trading.safetrade_simulator.Repositories.OrderRepository;
import com.trading.safetrade_simulator.Repositories.PositionRepository;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.enums.OrderStatus;
import com.trading.safetrade_simulator.model.OrderDetails;
import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.IIFLService;
import com.trading.safetrade_simulator.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IIFLService iiflService;


    //Only for BuyOrders
    @Override
    public boolean saveOrder(BuyOrderForm orderForm) {
//        System.out.println("1");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            //Get loged in user Details
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            String username = userDetail.getUsername();
            User user = userRepository.findByEmail(username).get();
            System.out.println("2 : " + user.getEmail());
            orderForm.setProductType(orderForm.getProductType().toUpperCase());
            System.out.println("order for " + orderForm);

            //Create new Order
                double amount = user.getWallet();
                OrderDetails order = new OrderDetails();
                order.setUser(user);
                order.setInstrumentDescription(orderForm.getDescription());
                order.setQuantity(orderForm.getQuantity() * orderForm.getLotSize());
                order.setPrice(orderForm.getPrice());
                order.setTotalAmount(orderForm.getAmount());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                order.setOrderType(orderForm.getOrderType());
                order.setOrderDuration(orderForm.getOrderDuration());

                if (orderForm.getStopLoss() != null) {
                    order.setStopLoss(orderForm.getStopLoss());
                }
                if (orderForm.getTargetPrice() != null) {
                    order.setTarget(orderForm.getTargetPrice());
                }

                //When Limit order
                if (orderForm.getProductType().equals("LIMIT")) {
                     order.setProductType(orderForm.getProductType());
                     if (orderForm.getLimitPrice() != null && orderForm.getLimitPrice() <= 0) {
                         order.setOrderStatus(OrderStatus.REJECTED);
                         orderRepository.save(order);
                         return true;
                     }
                     if (orderForm.getLimitPrice() != null) {
                        order.setLimitPrice(orderForm.getLimitPrice());
                        order.setPrice(orderForm.getLimitPrice());
                        order.setOrderStatus(OrderStatus.PENDING);
                        orderRepository.save(order);
                     }

               } else {
                    //When Market Order
                    order.setProductType(orderForm.getProductType());
                    if (orderForm.getAmount() <= amount) {
                        order.setOrderStatus(OrderStatus.PLACED);
                        if(orderForm.getOrderType().equals("BUY")){
                            amount = amount - order.getTotalAmount();
                            user.setWallet(amount);
                            userRepository.save(user);
                            orderRepository.save(order);

                            //Save to Positions
                            saveToPositions(order, user);
                        }


                    } else {
                        order.setOrderStatus(OrderStatus.REJECTED);
                        orderRepository.save(order);
                    }
            }
        } else {
            System.out.println("3 : No user  " );
            return false;
        }
        return true;
    }



    //For Buy Orders only
    public void saveToPositions(OrderDetails orderDetails, User user) {

        //To get weather this is a new order or existing one
        List<PositionOrders> positions = positionRepository.findByUserAndInstrumentDescription(user,
                orderDetails.getInstrumentDescription());
        PositionOrders position = null;
        for (PositionOrders pos : positions) {
            if (pos.getQuantity() != 0) {
                position = pos;
            }
        }
        //If this is existing one
        if (position != null && position.getQuantity() != 0 && position.getExitPrice()<=0) {
            //If both Buy orders.
            if (orderDetails.getOrderType().equals(position.getOrderType())) {
                position.setQuantity(position.getQuantity() + orderDetails.getQuantity());
                position.setTotalAmount(position.getTotalAmount() + orderDetails.getTotalAmount());
                position.setUpdatedAt(LocalDateTime.now());
                position.setStopLoss(orderDetails.getStopLoss());
                position.setTarget(orderDetails.getTarget());
                double avgprice = (position.getTotalAmount() + orderDetails.getTotalAmount())
                        / (position.getQuantity() + orderDetails.getQuantity());
                double avg = Math.round(avgprice * 100.0) / 100.0;
                position.setPrice(avg);
                positionRepository.save(position);
            }


            // If opposite orders ( Exit position) // In new method/controller
//            else {
//                // always exit whole position (All quantity)
//                 double amt = orderDetails.getTotalAmount();
//                position.setQuantity(0);
//                position.setPrice(orderDetails.getPrice());
//                position.setTotalAmount(position.getQuantity() * position.getPrice());
//                position.setUpdatedAt(LocalDateTime.now());
//                position.setOrderType(orderDetails.getOrderType());
//                position.setStopLoss(orderDetails.getStopLoss());
//                position.setTarget(orderDetails.getTarget());
//                user.setWallet(user.getWallet() + position.getTotalAmount());
//
//
//            }
//            userRepository.save(user);
//            positionRepository.save(position);
        }
        //If this is a new Position
        else {
            position = new PositionOrders();
            position.setInstrumentDescription(orderDetails.getInstrumentDescription());
            position.setQuantity(orderDetails.getQuantity());
            position.setPrice(orderDetails.getPrice());
            position.setTotalAmount(orderDetails.getTotalAmount());
            position.setCreatedAt(orderDetails.getCreatedAt());
            position.setUpdatedAt(orderDetails.getUpdatedAt());
            position.setOrderType(orderDetails.getOrderType());
            position.setOrderDuration(orderDetails.getOrderDuration());
            position.setProductType(orderDetails.getProductType());
            position.setStopLoss(orderDetails.getStopLoss());
            position.setTarget(orderDetails.getTarget());
            position.setOrderStatus(orderDetails.getOrderStatus());
            position.setUser(user);
//            userRepository.save(user);
            positionRepository.save(position);
        }
    }

    @Override
    public List<OrderDetails> getAllOrders() {
        List<OrderDetails> orders = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            String username = userDetail.getUsername();
            User user = userRepository.findByEmail(username).get();

            orders = orderRepository.findByUser(user);
        }
        for(OrderDetails orderDetails : orders){
            orderDetails.setUser(null);
        }
        return orders;

    }

    @Override
    public void executeLimitOrder() {
      List<OrderDetails> ordersList = orderRepository.findByOrderStatus(OrderStatus.PENDING);

      System.out.println("OrderList Pending : " + ordersList.size());

        for (OrderDetails order : ordersList) {
            String insturmentDescription = order.getInstrumentDescription();

            try {
                double ltp = iiflService.getSingleQuoteData(insturmentDescription);
                if (order.getLimitPrice() >= ltp && order.getLimitPrice() != 0) {
                    User user = order.getUser();
                    double amount = order.getQuantity() * ltp;
                    if (amount <= user.getWallet()) {
                        user.setWallet(user.getWallet() - amount);
                        order.setUpdatedAt(LocalDateTime.now());
                        order.setPrice(ltp);
                        order.setTotalAmount(ltp * order.getQuantity());
                        order.setOrderStatus(OrderStatus.PLACED);
                        saveToPositions(order, user);
                        orderRepository.save(order);
                        userRepository.save(user);
                    } else {
                        order.setOrderStatus(OrderStatus.REJECTED);
                    }
                }
            }catch (Exception ex){
                System.out.println("Error in ExecuteLimitOrder " + ex.getMessage());
            }
        }
    }

    @Override
    public void cancelPendingOrder() {
        List<OrderDetails> orders = orderRepository.findByOrderStatus(OrderStatus.PENDING);
        System.out.println("Inside cancel pending order : " + orders.size());
        for(OrderDetails order : orders){
            order.setOrderStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
        }
    }
}