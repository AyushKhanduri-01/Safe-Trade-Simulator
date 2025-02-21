package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.JwtResponse;
import com.trading.safetrade_simulator.DTO.LoginData;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginData loginData){
        System.out.println(loginData.getEmail() + "   " + loginData.getPassword() );
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(),loginData.getPassword()));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials.");
        }

        String accessToken = jwtService.generateToken(loginData.getEmail(), true);
        String refreshToken = jwtService.generateToken(loginData.getEmail(),false);
        User user = userRepository.findByEmail(loginData.getEmail()).get();
        JwtResponse jwtResponse = new JwtResponse(accessToken,refreshToken,user);

        return ResponseEntity.ok(jwtResponse);

    }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginData loginData){
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(),loginData.getPassword()));
//        String accessToken = jwtService.generateToken(loginData.getEmail(), true);
//        String refreshToken = jwtService.generateToken(loginData.getEmail(),false);
//        User user = userRepository.findByEmail(loginData.getEmail()).get();
//        JwtResponse jwtResponse = new JwtResponse(accessToken,refreshToken,user);
//
//        return ResponseEntity.ok(jwtResponse);
//
//    }





}
