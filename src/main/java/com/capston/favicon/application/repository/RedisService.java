package com.capston.favicon.application.repository;

public interface RedisService {
    void setCode(String email, String code);
    String getCode(String email);

    void deleteCode(String email);
}
