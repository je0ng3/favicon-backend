package com.capstone.favicon.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/** 세션 ID 를 헤더에서 꺼내는 규칙. 여기가 느슨해지면 임의 문자열로 세션 조회가 시도된다. */
class BearerHttpSessionIdResolverTest {

    private final BearerHttpSessionIdResolver resolver = new BearerHttpSessionIdResolver();

    private MockHttpServletRequest requestWith(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        return request;
    }

    @Test
    void resolves_the_value_after_the_bearer_prefix() {
        assertThat(resolver.resolveSessionIds(requestWith("Bearer session-id"))).containsExactly("session-id");
    }

    @Test
    void ignores_requests_without_a_usable_bearer_header() {
        assertThat(resolver.resolveSessionIds(requestWith(null))).isEmpty();
        assertThat(resolver.resolveSessionIds(requestWith(""))).isEmpty();
        assertThat(resolver.resolveSessionIds(requestWith("Basic session-id"))).isEmpty();
        assertThat(resolver.resolveSessionIds(requestWith("bearer session-id"))).isEmpty();
        assertThat(resolver.resolveSessionIds(requestWith("Bearer"))).isEmpty();
        assertThat(resolver.resolveSessionIds(requestWith("Bearer    "))).isEmpty();
    }
}
