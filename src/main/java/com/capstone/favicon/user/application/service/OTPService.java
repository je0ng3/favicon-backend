package com.capstone.favicon.user.application.service;

public interface OTPService {
    String generateOTP(String email);
    boolean verifyOTP(String email, String otp);
}
