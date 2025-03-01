package com.capston.favicon.application.repository;

import com.capston.favicon.domain.domain.User;
import com.capston.favicon.domain.dto.LoginDto;
import com.capston.favicon.domain.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void sendCode(RegisterDto.checkEmail checkEmail);
    void checkCode(RegisterDto.checkCode checkCode);
    void join(RegisterDto registerDto);
    void login(LoginDto loginDto, HttpServletRequest request);
    void logout(HttpServletRequest request);
}
