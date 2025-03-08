package com.capston.favicon.user.application.service;

import com.capston.favicon.user.dto.LoginDto;
import com.capston.favicon.user.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void sendCode(RegisterDto.checkEmail checkEmail);
    void checkCode(RegisterDto.checkCode checkCode);
    void join(RegisterDto registerDto);
    void login(LoginDto loginDto, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
