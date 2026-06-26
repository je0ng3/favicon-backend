package com.capstone.favicon.user.application;

import com.capstone.favicon.security.JwtUtil;
import com.capstone.favicon.security.RefreshToken;
import com.capstone.favicon.user.application.service.MailService;
import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.application.service.UserService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RefreshRequest;
import com.capstone.favicon.user.dto.RegisterDto;
import com.capstone.favicon.user.repository.RefreshTokenRepository;
import com.capstone.favicon.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.admin-emails}")
    private String adminEmailsStr;
    private Set<String> adminEmails;

    @PostConstruct
    public void init() {
        adminEmails = Arrays.stream(adminEmailsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

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
        User user = new User();
        String email = registerDto.getEmail();
        user.setEmail(email);
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        if (adminEmails.contains(registerDto.getEmail())) {
            user.setRole(1); // admin
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null || !passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 이메일 또는 비번입니다.");
        }

        String token = jwtUtil.createAccessToken(user);
        String refresh = issueAndStoreRefreshToken(user);
        return new LoginResponseDto(user.getUserId(), user.getUsername(), token, refresh);
    }

    @Override
    @Transactional
    public void delete(User user) {
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        refreshTokenRepository.deleteByUserId(user.getUserId());
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public LoginResponseDto refreshToken(RefreshRequest request) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 refresh 토큰입니다."));

        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByUserId(tokenEntity.getUserId());
            throw new BadCredentialsException("refresh 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        User user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 유저입니다."));

        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = issueAndStoreRefreshToken(user);   // 토큰 회전(rotation)

        return new LoginResponseDto(user.getUserId(), user.getUsername(), newAccessToken, newRefreshToken);
    }

    // 사용자당 refresh 토큰 1개만 유지하도록 기존 토큰을 지우고 새로 저장
    private String issueAndStoreRefreshToken(User user) {
        String refresh = jwtUtil.createRefreshToken(user);
        refreshTokenRepository.deleteByUserId(user.getUserId());
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getUserId())
                .userEmail(user.getEmail())
                .token(refresh)
                .expiryDate(LocalDateTime.now().plusDays(7))   // JwtUtil의 REFRESH_TOKEN_EXP_MS(7일)와 일치
                .build());
        return refresh;
    }

}
