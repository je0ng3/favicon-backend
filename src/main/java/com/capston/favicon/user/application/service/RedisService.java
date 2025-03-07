package com.capston.favicon.user.application.service;

public interface RedisService {
    void setCode(String email, String code);
    String getCode(String email);

    void deleteCode(String email);
}
