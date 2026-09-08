package com.capstone.favicon.user.application.service;

import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.RegisterDto;



public interface UserService {
    void sendCode(RegisterDto.checkEmail checkEmail);
    void checkCode(RegisterDto.checkCode checkCode);
    void join(RegisterDto registerDto);

    /** 자격 증명만 검증한다. 세션 생성은 컨트롤러의 SessionAuthenticator 담당. */
    User login(LoginDto loginDto);
    void delete(User user);
}
