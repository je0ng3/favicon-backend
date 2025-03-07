package com.capston.favicon.user.application.service;

import com.capston.favicon.user.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void login(LoginDto loginDto, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
