package com.trading.safetrade_simulator.Repositories;

import com.trading.safetrade_simulator.enums.OrderStatus;
import com.trading.safetrade_simulator.model.OrderDetails;
import com.trading.safetrade_simulator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends MongoRepository<OrderDetails,String> {

    Collection<OrderDetails> findByUserAndOrderStatusIn(User user, List<OrderStatus> statuses);
    List<OrderDetails> findByOrderStatus(OrderStatus status);
    List<OrderDetails> findByUser(User user);


}