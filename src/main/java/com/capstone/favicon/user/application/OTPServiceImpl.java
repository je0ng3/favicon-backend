package com.capstone.favicon.user.application;

import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.application.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final RedisService redisService;

    @Override
    public String generateOTP(String email) {
        String otp = ThreadLocalRandom.current().ints(0, 10)
                .limit(6)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        redisService.setCode(email, otp);
        return otp;
    }

    @Override
    public boolean verifyOTP(String email, String otp){
        String storedOtp = redisService.getCode(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisService.deleteCode(email);
            return true;
        }
        return false;
    }
}
