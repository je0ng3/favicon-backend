package com.capstone.favicon.security;

import com.capstone.favicon.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 토큰 발급·검증은 인증의 핵심인데 만료·위조 경로가 테스트로 고정되어 있지 않았다.
 * 외부 의존성이 UserDetailsService 하나뿐이라 컨텍스트 없이 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private static final String SECRET = "test-jwt-secret-value-long-enough-for-hs256-algorithm";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private JwtUtil jwtUtil;
    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, userDetailsService);

        user = new User();
        user.setUserId(1L);
        user.setEmail("user@test.com");
        user.setUsername("tester");
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
    }

    private String signedWith(SecretKey key, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .subject("1")
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // == 토큰 생성 ==

    @Test
    void accessToken_carries_userId_and_email() {
        Claims claims = parse(jwtUtil.createAccessToken(user));

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(((Number) claims.get("userId")).longValue()).isEqualTo(1L);
        assertThat(claims.get("userEmail", String.class)).isEqualTo("user@test.com");
    }

    @Test
    void accessToken_expires_in_an_hour_and_refreshToken_in_a_week() {
        long now = System.currentTimeMillis();
        long accessTtl = parse(jwtUtil.createAccessToken(user)).getExpiration().getTime() - now;
        long refreshTtl = parse(jwtUtil.createRefreshToken(user)).getExpiration().getTime() - now;

        assertThat(accessTtl).isBetween(Duration.ofMinutes(59).toMillis(), Duration.ofMinutes(61).toMillis());
        assertThat(refreshTtl).isBetween(Duration.ofDays(6).toMillis(), Duration.ofDays(8).toMillis());
        assertThat(refreshTtl).isGreaterThan(accessTtl);
    }

    @Test
    void tokens_are_not_readable_without_the_signing_key() {
        String token = jwtUtil.createAccessToken(user);
        SecretKey otherKey = Keys.hmacShaKeyFor("a-completely-different-secret-value-for-hs256".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> Jwts.parser().verifyWith(otherKey).build().parseSignedClaims(token))
                .isInstanceOf(JwtException.class);
    }

    // == 검증 ==

    @Test
    void validateToken_accepts_a_token_this_util_issued() {
        String token = jwtUtil.createAccessToken(user);

        assertThat(jwtUtil.validateToken(token).getPayload().getSubject()).isEqualTo("1");
    }

    @Test
    void validateToken_rejects_expired_token() {
        long now = System.currentTimeMillis();
        String expired = signedWith(KEY, new Date(now - Duration.ofHours(2).toMillis()),
                new Date(now - Duration.ofHours(1).toMillis()));

        assertThatThrownBy(() -> jwtUtil.validateToken(expired))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void validateToken_rejects_token_signed_with_another_key() {
        SecretKey forgedKey = Keys.hmacShaKeyFor("forged-secret-value-long-enough-for-hs256-alg".getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        String forged = signedWith(forgedKey, new Date(now), new Date(now + Duration.ofHours(1).toMillis()));

        assertThatThrownBy(() -> jwtUtil.validateToken(forged)).isInstanceOf(JwtException.class);
    }

    @Test
    void validateToken_rejects_malformed_and_blank_tokens() {
        assertThatThrownBy(() -> jwtUtil.validateToken("not-a-jwt")).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> jwtUtil.validateToken("")).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> jwtUtil.validateToken(null)).isInstanceOf(JwtException.class);
    }

    @Test
    void getUserEmail_reads_the_claim() {
        assertThat(jwtUtil.getUserEmail(jwtUtil.createAccessToken(user))).isEqualTo("user@test.com");
    }

    // == 헤더에서 토큰 추출 ==

    @Test
    void resolveToken_strips_the_bearer_prefix() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer abc.def.ghi");

        assertThat(jwtUtil.resolveToken(request)).isEqualTo("abc.def.ghi");
    }

    @Test
    void resolveToken_returns_null_when_header_is_missing_or_not_bearer() {
        assertThat(jwtUtil.resolveToken(new MockHttpServletRequest())).isNull();

        MockHttpServletRequest basic = new MockHttpServletRequest();
        basic.addHeader("Authorization", "Basic abc.def.ghi");
        assertThat(jwtUtil.resolveToken(basic)).isNull();

        MockHttpServletRequest blank = new MockHttpServletRequest();
        blank.addHeader("Authorization", "");
        assertThat(jwtUtil.resolveToken(blank)).isNull();
    }

    // == Authentication 생성 ==

    @Test
    void getAuthentication_loads_the_user_behind_the_token() {
        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("user@test.com").password("encoded-pw").authorities("ROLE_USER").build();
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(details);

        Authentication auth = jwtUtil.getAuthentication(jwtUtil.createAccessToken(user));

        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getPrincipal()).isEqualTo(details);
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
        // 자격증명은 컨텍스트에 남기지 않는다
        assertThat(auth.getCredentials()).isNull();
    }

    @Test
    void getAuthentication_does_not_touch_the_user_store_for_an_invalid_token() {
        assertThatThrownBy(() -> jwtUtil.getAuthentication("not-a-jwt")).isInstanceOf(JwtException.class);

        // 검증이 조회보다 먼저여야 한다. 순서가 뒤집히면 위조 토큰으로도 조회가 돈다
        verifyNoInteractions(userDetailsService);
    }
}
