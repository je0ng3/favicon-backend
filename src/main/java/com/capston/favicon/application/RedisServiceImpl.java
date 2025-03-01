package com.capston.favicon.application;

import com.capston.favicon.application.repository.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 3, TimeUnit.MINUTES);
    }

    @Override
    public String getCode(String email) {
        Object code = redisTemplate.opsForValue().get(email);
        return code == null ? null : code.toString();
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }
}
