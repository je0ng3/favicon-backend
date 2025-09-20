package com.capstone.favicon.security;

import com.capstone.favicon.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXP_MS = 1000 * 60 * 60; // 토큰 만료 시간 1시간
    private static final long REFRESH_TOKEN_EXP_MS = 1000 * 60 * 60 * 24 * 7; // 7일

    private final UserDetailsServiceImpl userDetailsService;

    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey,
                   UserDetailsServiceImpl userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.userDetailsService = userDetailsService;
    }

    // ==토큰 생성==
    public String createAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_EXP_MS);
    }

    public String createRefreshToken(User user) {
        return createToken(user, REFRESH_TOKEN_EXP_MS);
    }

    private String createToken(User user, long expirationMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        Map<String, Object> claims = Map.of(
                "userId", user.getUserId(),
                "userEmail", user.getEmail()
        );
        log.debug("유저 {}를 위한 JWT 발급", user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // == 토큰 파싱/검증 ==
    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT token is expired", e);
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException("JWT signature is invalid", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("JWT token is unsupported", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT token is invalid", e);
        }
    }

    public String getUserEmail(String token) {
        Claims claims = validateToken(token).getBody();
        return claims.get("userEmail", String.class);
    }


    // == Spring Security Authentication ==
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        String email = getUserEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // == 토큰 추출 ==
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}