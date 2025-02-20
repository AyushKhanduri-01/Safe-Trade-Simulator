package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.DTO.UserDTO;
import com.trading.safetrade_simulator.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    boolean isUserPresent(String email);

    void registerUser(UserDTO user);



}
