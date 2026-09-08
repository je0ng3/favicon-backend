package com.capstone.favicon.user.application;

import com.capstone.favicon.user.application.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    // 세션(spring:session:*)과 같은 Redis 를 쓰므로 키 충돌 방지용 prefix
    private static final String OTP_KEY_PREFIX = "otp:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setCode(String email, String code) {
        redisTemplate.opsForValue().set(key(email), code, 3, TimeUnit.MINUTES);
    }

    @Override
    public String getCode(String email) {
        Object code = redisTemplate.opsForValue().get(key(email));
        return code == null ? null : code.toString();
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(key(email));
    }

    private String key(String email) {
        return OTP_KEY_PREFIX + email;
    }
}
