package com.trading.safetrade_simulator.service.impl;

import com.trading.safetrade_simulator.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {

    @Autowired
    private  JavaMailSender javaMailSender;
    @Override
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    public boolean sendMail(String otp,String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is : " + otp);
        try {
            javaMailSender.send(message);
            return  true;
        }catch (Exception ex){
            return false;
        }


    }
}
