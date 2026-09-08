package com.capstone.favicon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 세션 조회는 SessionRepositoryFilter 안에서 일어나 GlobalExceptionHandler 가 닿지 않는다.
 * 그대로 두면 Redis 장애가 500 으로 나가므로 여기서 503 으로 바꾼다.
 * 401 이 아닌 이유: 세션 자체는 살아 있을 수 있어서, 클라이언트가 토큰을 버리면 안 된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisUnavailableFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RedisConnectionFailureException | QueryTimeoutException | RedisSystemException e) {
            log.error("Redis 접근 실패로 요청을 처리하지 못했다: {} {}", request.getMethod(), request.getRequestURI(), e);
            respondUnavailable(response);
        }
    }

    private void respondUnavailable(HttpServletResponse response) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.reset();
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                APIResponse.errorAPI("일시적으로 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요."));
    }
}
