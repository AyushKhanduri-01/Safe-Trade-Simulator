package com.trading.safetrade_simulator.Repositories;

import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PositionRepository extends MongoRepository<PositionOrders, String> {
    List<PositionOrders> findByUserAndInstrumentDescription(User user, String instrumentDascription);

    List<PositionOrders> findByUser(User user);

    int countByUser(User user);

    int countByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    int countByUserAndExitPrice(User user, double exitPrice);


    @Query("{ 'exitPrice': 0, $or: [ { 'stopLoss': { $gte: 0 } }, { 'target': { $gte: 0 } } ] }")
    List<PositionOrders> findByExitPriceZeroAndStopLossOrTargetSet();

    @Query("{ 'exitPrice' : 0 }")
    List<PositionOrders> findByExitPriceZero();

    List<PositionOrders> findByUserIdAndCreatedAtBetween(User user, LocalDate startDate, LocalDate endDate);

    List<PositionOrders> findByUserAndCreatedAtBetween(User user, Date startOfDay, Date endOfDay);

    public PositionOrders findById();
}
