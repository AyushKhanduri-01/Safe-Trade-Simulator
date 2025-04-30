package com.trading.safetrade_simulator.Repositories;

import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PositionRepository extends MongoRepository<PositionOrders, String> {
    List<PositionOrders> findByUserAndInstrumentDescription(User user, String instrumentDascription);

    int countByUser(User user);

    int countByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
