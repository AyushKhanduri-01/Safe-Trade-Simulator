package com.trading.safetrade_simulator.service;

public interface OTPService {
    public String generateOTP();
    public boolean sendMail(String otp,String email);
}
