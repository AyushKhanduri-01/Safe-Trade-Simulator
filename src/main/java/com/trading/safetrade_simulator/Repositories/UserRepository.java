package com.trading.safetrade_simulator.Repositories;

import com.trading.safetrade_simulator.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    boolean existsByEmail(String email);

    Optional<User>  findByEmail(String email);
}
