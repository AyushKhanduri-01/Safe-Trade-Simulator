package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.UserDTO;
import com.trading.safetrade_simulator.enums.Roles;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.OTPService;
import com.trading.safetrade_simulator.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserRegistration {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OTPService otpService;
    @Autowired
    private UserService userService;

    //For generating otp and sent otp to email and frontend to matching and confirmation
    @PostMapping("/validateOtp")
    public ResponseEntity<?> regesterUserOtp(@Valid @RequestBody UserDTO userDTO, BindingResult result){

        if(result.hasErrors()){
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        if(userService.isUserPresent(userDTO.getEmail())){
            return new ResponseEntity<>("Email is already registered",HttpStatus.BAD_REQUEST);
        }
        String otp = otpService.generateOTP();
        if(otpService.sendMail(otp,userDTO.getEmail())){
            return new ResponseEntity<>("otp : "+ otp, HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>("Failed to send OTP",HttpStatus.BAD_REQUEST);
        }
    }

    //For registering user once its otp matched
    @PostMapping("/register")
    public ResponseEntity<?> regesterUser(@Valid @RequestBody UserDTO userDTO, BindingResult result){

        if(result.hasErrors()){
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        if(userService.isUserPresent(userDTO.getEmail())){
            return new ResponseEntity<>("Email is alredy registered",HttpStatus.BAD_REQUEST);
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userService.registerUser(userDTO);
        return new ResponseEntity<>("User Registered Succesfully",HttpStatus.CREATED);
    }
}
