package com.capstone.favicon.application;

import com.capstone.favicon.security.UserSessionRegistry;
import com.capstone.favicon.user.application.UserServiceImpl;
import com.capstone.favicon.user.application.service.MailService;
import com.capstone.favicon.user.application.service.OTPService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.dto.LoginDto;
import com.capstone.favicon.user.dto.RegisterDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 인증 핵심 흐름(자격 증명 검증, 탈퇴 시 세션 만료)을 외부 의존성 없이 검증하는 단위 테스트.
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
    private UserSessionRegistry userSessionRegistry;

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

    private LoginDto loginDto(String email, String password) {
        LoginDto dto = new LoginDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    // == 로그인 ==

    @Test
    void login_success_returns_the_authenticated_user() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("raw-pw", "encoded-pw")).thenReturn(true);

        assertThat(userService.login(loginDto("user@test.com", "raw-pw"))).isSameAs(user);
    }

    @Test
    void login_with_wrong_password_throws() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-pw", "encoded-pw")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginDto("user@test.com", "wrong-pw")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_with_unknown_email_throws_same_exception_as_wrong_password() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(null);

        // 이메일 존재 여부가 응답으로 구분되지 않도록 동일한 예외를 던진다
        assertThatThrownBy(() -> userService.login(loginDto("nobody@test.com", "raw-pw")))
                .isInstanceOf(BadCredentialsException.class);
    }

    // == 탈퇴 ==

    @Test
    void delete_expires_sessions_before_removing_the_account() {
        userService.delete(user);

        // 순서가 뒤집히면 그 사이 요청이 이미 지워진 계정으로 인증될 수 있다
        var order = inOrder(userSessionRegistry, userRepository);
        order.verify(userSessionRegistry).expireAll("user@test.com");
        order.verify(userRepository).delete(user);
    }

    @Test
    void delete_of_unauthenticated_caller_throws_and_touches_nothing() {
        assertThatThrownBy(() -> userService.delete(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userSessionRegistry, org.mockito.Mockito.never()).expireAll(anyString());
        verify(userRepository, org.mockito.Mockito.never()).delete(org.mockito.ArgumentMatchers.any());
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
