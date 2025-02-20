package com.trading.safetrade_simulator.service.impl;

import com.trading.safetrade_simulator.DTO.UserDTO;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.enums.Roles;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;


    @Override
    public boolean isUserPresent(String email) {
       return userRepository.existsByEmail(email);
    }

    @Override
    public void registerUser(UserDTO userDTO) {
        User user = new User(userDTO.getName(),userDTO.getEmail(),userDTO.getPassword());
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        CustomUserDetail customUserDetail = new CustomUserDetail(user);
        return  customUserDetail;
    }
}
