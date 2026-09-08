package com.capstone.favicon.security;

import com.capstone.favicon.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/** 로그인/로그아웃 시점의 세션 생성·폐기. Spring Security 가 자동 인증을 하지 않으므로 직접 저장한다. */
@Component
@RequiredArgsConstructor
public class SessionAuthenticator {

    private final SecurityContextRepository securityContextRepository;

    /** 인증된 사용자로 새 세션을 열고 세션 ID 반환 */
    public String startSession(User user, HttpServletRequest request, HttpServletResponse response) {
        // 세션 고정 공격 방지: 로그인 전 세션은 버린다
        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return request.getSession().getId();
    }

    public void endSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
