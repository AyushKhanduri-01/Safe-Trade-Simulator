package com.trading.safetrade_simulator.service.impl;

import com.trading.safetrade_simulator.DTO.DashboardData;
import com.trading.safetrade_simulator.Repositories.OrderRepository;
import com.trading.safetrade_simulator.Repositories.PositionRepository;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PositionRepository positionRepository;


    @Override
    public DashboardData getDashboardData(Authentication authentication) {
       DashboardData dashboardData = new DashboardData();
       CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
       String username =  userDetail.getUsername();
       User user = userRepository.findByEmail(username).get();

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

       if(user != null){
           dashboardData.setName(user.getName());
           dashboardData.setEmail(user.getEmail());
           dashboardData.setRemainingFund(user.getWallet());
           dashboardData.setTotalOrders(orderRepository.countByUser(user));
           dashboardData.setTodayOrders(orderRepository.countByUserAndCreatedAtBetween(user,startOfDay,endOfDay));
           dashboardData.setTotalPositions(positionRepository.countByUser(user));
           dashboardData.setTodayPosition(positionRepository.countByUserAndCreatedAtBetween(user,startOfDay,endOfDay));
           dashboardData.setActivePosition(positionRepository.countByUserAndExitPrice(user,0.0));


       }
       System.out.println("Dashboard data : "+ dashboardData);

       return dashboardData;
    }
}
