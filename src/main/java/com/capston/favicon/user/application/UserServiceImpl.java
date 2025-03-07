package com.capston.favicon.user.application;

import com.capston.favicon.user.application.service.MailService;
import com.capston.favicon.user.application.service.OTPService;
import com.capston.favicon.user.application.service.UserService;
import com.capston.favicon.user.domain.User;
import com.capston.favicon.user.dto.LoginDto;
import com.capston.favicon.user.dto.RegisterDto;
import com.capston.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private OTPService otpService;



    @Override
    public void sendCode(RegisterDto.checkEmail checkEmail) {
        String email = checkEmail.getEmail();
        if (userRepository.findByEmail(email)!=null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다. 재확인해주세요.");
        }
        String otp = otpService.generateOTP(email);
        mailService.send(email, "[Favicon] 회원가입 인증번호", "인증번호: "+otp);
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
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        userRepository.save(user);
    }

    @Override
    public void login(LoginDto loginDto, HttpServletRequest request) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
        } else {
            throw new BadCredentialsException("Wrong password");
        }

    }

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("username");
    }

}
