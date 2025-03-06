package com.capston.favicon.application.repository;

import com.capston.favicon.domain.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void login(LoginDto loginDto, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
