package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;


public interface UserService {
    void sendCode(RegisterDto.checkEmail checkEmail);
    void checkCode(RegisterDto.checkCode checkCode);
    void join(RegisterDto registerDto);
    String login(LoginDto loginDto, HttpServletRequest request);
    void logout(HttpServletRequest request);
    void delete(HttpServletRequest request);
    boolean checkAdmin(HttpServletRequest request);
}
