package com.capstone.favicon.application;

import com.capstone.favicon.security.JwtUtil;
import com.capstone.favicon.security.RefreshToken;
import com.capstone.favicon.user.application.UserServiceImpl;
import com.capstone.favicon.user.application.service.MailService;
import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.LoginResponseDto;
import com.capstone.favicon.user.dto.RefreshRequest;
import com.capstone.favicon.user.dto.RegisterDto;
import com.capstone.favicon.user.repository.RefreshTokenRepository;
import com.capstone.favicon.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 인증 핵심 흐름(로그인, refresh 토큰 재발급/회전)을 외부 의존성 없이 검증하는 단위 테스트.
 * DB·Redis 를 띄우지 않으므로 CI 의 gradle build 단계에서 가볍게 함께 돈다.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @Mock
    private OTPService otpService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("user@test.com");
        user.setUsername("tester");
        user.setPassword("encoded-pw");
    }

    // == 로그인 ==

    @Test
    void login_success_returns_tokens_and_rotates_stored_refresh_token() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@test.com");
        loginDto.setPassword("raw-pw");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("raw-pw", "encoded-pw")).thenReturn(true);
        when(jwtUtil.createAccessToken(user)).thenReturn("access-token");
        when(jwtUtil.createRefreshToken(user)).thenReturn("refresh-token");

        LoginResponseDto response = userService.login(loginDto);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefresh()).isEqualTo("refresh-token");
        assertThat(response.getUserId()).isEqualTo(1L);

        // 사용자당 1개 유지: 기존 토큰 삭제 후 새 토큰 저장 순서까지 보장
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        var order = inOrder(refreshTokenRepository);
        order.verify(refreshTokenRepository).deleteByUserId(1L);
        order.verify(refreshTokenRepository).save(saved.capture());
        assertThat(saved.getValue().getToken()).isEqualTo("refresh-token");
        assertThat(saved.getValue().getUserId()).isEqualTo(1L);
        assertThat(saved.getValue().getExpiryDate()).isAfter(LocalDateTime.now());
    }

    @Test
    void login_with_wrong_password_throws_and_issues_no_token() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@test.com");
        loginDto.setPassword("wrong-pw");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-pw", "encoded-pw")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtUtil, never()).createAccessToken(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void login_with_unknown_email_throws_same_exception_as_wrong_password() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("nobody@test.com");
        loginDto.setPassword("raw-pw");

        when(userRepository.findByEmail("nobody@test.com")).thenReturn(null);

        // 이메일 존재 여부가 응답으로 구분되지 않도록 동일한 예외를 던진다
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtUtil, never()).createAccessToken(any());
    }

    // == refresh 토큰 재발급 ==

    private RefreshToken storedToken(LocalDateTime expiryDate) {
        return RefreshToken.builder()
                .id(10L)
                .userId(1L)
                .userEmail("user@test.com")
                .token("old-refresh")
                .expiryDate(expiryDate)
                .build();
    }

    @Test
    void refresh_with_valid_token_rotates_to_new_refresh_token() {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("old-refresh");

        when(refreshTokenRepository.findByToken("old-refresh"))
                .thenReturn(Optional.of(storedToken(LocalDateTime.now().plusDays(1))));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtUtil.createAccessToken(user)).thenReturn("new-access");
        when(jwtUtil.createRefreshToken(user)).thenReturn("new-refresh");

        LoginResponseDto response = userService.refreshToken(request);

        assertThat(response.getToken()).isEqualTo("new-access");
        assertThat(response.getRefresh()).isEqualTo("new-refresh");

        // 회전: 기존 토큰이 삭제되어 재사용이 불가능해야 한다
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        var order = inOrder(refreshTokenRepository);
        order.verify(refreshTokenRepository).deleteByUserId(1L);
        order.verify(refreshTokenRepository).save(saved.capture());
        assertThat(saved.getValue().getToken()).isEqualTo("new-refresh");
    }

    @Test
    void refresh_with_unknown_token_throws() {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("forged-refresh");

        when(refreshTokenRepository.findByToken("forged-refresh")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtUtil, never()).createAccessToken(any());
    }

    @Test
    void refresh_with_expired_token_deletes_it_and_forces_relogin() {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("old-refresh");

        when(refreshTokenRepository.findByToken("old-refresh"))
                .thenReturn(Optional.of(storedToken(LocalDateTime.now().minusMinutes(1))));

        assertThatThrownBy(() -> userService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class);

        // 만료 토큰은 DB 에서도 제거되어 이후 시도 자체가 불가능해야 한다
        verify(refreshTokenRepository).deleteByUserId(1L);
        verify(refreshTokenRepository, never()).save(any());
        verify(jwtUtil, never()).createAccessToken(any());
    }

    // == 회원가입 ==

    @Test
    void join_encodes_password_and_grants_admin_role_only_to_configured_emails() {
        ReflectionTestUtils.setField(userService, "adminEmailsStr", "admin@test.com, boss@test.com");
        userService.init();
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pw");

        RegisterDto adminDto = new RegisterDto();
        adminDto.setEmail("admin@test.com");
        adminDto.setUsername("admin");
        adminDto.setPassword("raw-pw");

        RegisterDto normalDto = new RegisterDto();
        normalDto.setEmail("user@test.com");
        normalDto.setUsername("tester");
        normalDto.setPassword("raw-pw");

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);

        userService.join(adminDto);
        userService.join(normalDto);
        verify(userRepository, org.mockito.Mockito.times(2)).save(saved.capture());

        User savedAdmin = saved.getAllValues().get(0);
        User savedNormal = saved.getAllValues().get(1);
        assertThat(savedAdmin.getRole()).isEqualTo(1);
        assertThat(savedNormal.getRole()).isEqualTo(0);
        assertThat(savedNormal.getPassword()).isEqualTo("encoded-pw"); // 평문 저장 금지
    }
}
