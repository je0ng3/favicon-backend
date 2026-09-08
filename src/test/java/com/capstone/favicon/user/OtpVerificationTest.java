package com.capstone.favicon.user;

import com.capstone.favicon.user.application.OTPServiceImpl;
import com.capstone.favicon.user.application.RedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 가입용 OTP 는 발급·검증·폐기가 한 세트다. 저장소(RedisServiceImpl)까지 실물로 엮어
 * 키 규칙과 1회용 보장을 같이 고정한다.
 */
@ExtendWith(MockitoExtension.class)
class OtpVerificationTest {

    private static final String EMAIL = "user@test.com";
    private static final String KEY = "otp:" + EMAIL;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    private OTPServiceImpl otpService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        otpService = new OTPServiceImpl(new RedisServiceImpl(redisTemplate));
    }

    @Test
    void a_generated_code_is_six_digits_and_stored_under_a_prefixed_key_for_three_minutes() {
        String otp = otpService.generateOTP(EMAIL);

        assertThat(otp).matches("\\d{6}");
        // prefix 가 빠지면 이메일 자체가 키가 되어 세션 키와 같은 공간을 쓴다
        verify(valueOperations).set(KEY, otp, 3, TimeUnit.MINUTES);
    }

    @Test
    void a_matching_code_verifies_once_and_is_then_discarded() {
        when(valueOperations.get(KEY)).thenReturn("123456");

        assertThat(otpService.verifyOTP(EMAIL, "123456")).isTrue();
        verify(redisTemplate).delete(KEY);
    }

    @Test
    void a_wrong_code_fails_and_leaves_the_stored_code_alone() {
        when(valueOperations.get(KEY)).thenReturn("123456");

        // 여기서 지워버리면 오타 한 번에 재발급을 받아야 한다
        assertThat(otpService.verifyOTP(EMAIL, "000000")).isFalse();
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void an_expired_or_absent_code_fails() {
        when(valueOperations.get(KEY)).thenReturn(null);

        assertThat(otpService.verifyOTP(EMAIL, "123456")).isFalse();
        verify(redisTemplate, never()).delete(anyString());
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }
}
