package com.capstone.favicon.user.application;

import com.capstone.favicon.user.application.service.MailService;
import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.MonthlyCountDto;
import com.capstone.favicon.user.dto.RegisterDto;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.round;


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
    public void login(LoginDto loginDto, HttpServletRequest request) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        if (user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("id", user.getUserId());
        } else {
            throw new BadCredentialsException("Wrong password");
        }

    }

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("id");
    }

    @Override
    public void delete(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long id = (Long) session.getAttribute("id");
        User user = userRepository.findByUserId(id);
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

    @Override
    public Map<String, Object> getUserCount() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.minusMonths(1).withDayOfMonth(1);
        LocalDateTime end = start.plusMonths(1);

        int previousCount = userRepository.countUsersAt(start, end);
        int currentCount = userRepository.countUsersAt(end, today.plusDays(1));

        double rate = 0.0;
        if (previousCount > 0) {
            rate = ((double) (currentCount-previousCount) / previousCount) *100.0;
            rate = Math.round(rate*10.0)/10.0;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", userRepository.countAllUsers());
        result.put("rate", rate);
        return result;
    }

    @Override
    public List<MonthlyCountDto> getUserOverview() {
        LocalDateTime end = LocalDateTime.now().plusMonths(1).withDayOfMonth(1);
        List<MonthlyCountDto> result = new ArrayList<>();
        for (int i = 0; i<6; i++) {
            LocalDateTime start = end.minusMonths(1);
            int month = start.getMonthValue();
            int count = userRepository.countUsersAt(start, end);
            result.add(new MonthlyCountDto(month, count));
            end = start;
        }
        return result;
    }

}
