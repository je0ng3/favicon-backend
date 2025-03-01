package com.capston.favicon.application.repository;

public interface OTPService {
    String generateOTP(String email);
    boolean verifyOTP(String email, String otp);
}
