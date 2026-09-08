package com.capstone.favicon.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 세션 ID 를 쿠키 대신 Authorization: Bearer 헤더로 주고받는다.
 * 쿠키를 안 쓰므로 CSRF 대상이 아니고, 발급은 로그인 응답 body 로만 나가서 set 계열은 no-op 이다.
 */
public class BearerHttpSessionIdResolver implements HttpSessionIdResolver {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(PREFIX)) {
            return Collections.emptyList();
        }
        String sessionId = header.substring(PREFIX.length()).trim();
        return sessionId.isEmpty() ? Collections.emptyList() : List.of(sessionId);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        // 로그인 응답 body 로 전달
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        // 클라이언트가 저장한 값을 버린다
    }
}
