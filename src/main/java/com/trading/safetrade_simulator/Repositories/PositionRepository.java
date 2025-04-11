package com.trading.safetrade_simulator.Repositories;

import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PositionRepository extends MongoRepository<PositionOrders, String> {
    List<PositionOrders> findByUserAndInstrumentDescription(User user, String instrumentDascription);
}
