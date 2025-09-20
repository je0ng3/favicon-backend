package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RegisterDto;



public interface UserService {
    void sendCode(RegisterDto.checkEmail checkEmail);
    void checkCode(RegisterDto.checkCode checkCode);
    void join(RegisterDto registerDto);
    LoginResponseDto login(LoginDto loginDto);
    void delete(User user);
}
