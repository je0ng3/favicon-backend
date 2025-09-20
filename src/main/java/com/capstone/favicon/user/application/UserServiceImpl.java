package com.capstone.favicon.user.application;

import com.capstone.favicon.security.JwtUtil;
import com.capstone.favicon.user.application.service.MailService;
import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RegisterDto;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private OTPService otpService;

    private final JwtUtil jwtUtil;

    @Override
    public void sendCode(RegisterDto.checkEmail checkEmail) {
        String email = checkEmail.getEmail();
        if (userRepository.findByEmail(email)!=null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다. 재확인해주세요.");
        }
        String otp = otpService.generateOTP(email);
        mailService.send(email, "[Favicon] 회원가입 인증번호", otp);
    }

    @Override
    public void checkCode(RegisterDto.checkCode checkCode) {
        String email = checkCode.getEmail();
        String code = checkCode.getCode();
        if (! otpService.verifyOTP(email, code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
    }

    @Override
    public void join(RegisterDto registerDto) {
        if (registerDto == null) {
            throw new RuntimeException();
        }
        Set<String> adminEmail = Set.of("test@gmail.com");
        User user = new User();
        String email = registerDto.getEmail();
        user.setEmail(email);
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        if (adminEmail.contains(email)) {
            user.setRole(1);
        }
        userRepository.save(user);
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        if (user.getPassword().equals(password)) {
            String token = jwtUtil.createAccessToken(user);
            return new LoginResponseDto(user.getUserId(), user.getUsername(), token);
        } else {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public void delete(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long id = (Long) session.getAttribute("id");
        User user = userRepository.findByUserId(id);
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findByUserId(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        userRepository.delete(user);
    }


    @Override
    public boolean checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long id = (Long) session.getAttribute("id");
        User user = userRepository.findByUserId(id);
        Integer role = user.getRole();
        if (role == 1) {
            return true;
        }
        return false;
    }

}
