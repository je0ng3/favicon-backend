package com.capstone.favicon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Redis 장애가 500 이 아니라 503 으로 나가는지. 세션 조회는 필터 안이라 @RestControllerAdvice 가 못 잡는다. */
class RedisUnavailableFilterTest {

    private final RedisUnavailableFilter filter = new RedisUnavailableFilter(new ObjectMapper());

    private MockHttpServletResponse filterWith(RuntimeException thrown) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/request/list"), response,
                (req, res) -> { throw thrown; });
        return response;
    }

    @Test
    void redis_failures_become_503_with_the_common_error_body() throws Exception {
        for (RuntimeException e : new RuntimeException[]{
                new RedisConnectionFailureException("down"),
                new QueryTimeoutException("timeout")}) {
            MockHttpServletResponse response = filterWith(e);

            assertThat(response.getStatus()).isEqualTo(503);
            assertThat(response.getContentAsString()).contains("\"status\":\"error\"");
        }
    }

    @Test
    void unrelated_failures_are_left_alone() {
        // 여기서 삼키면 DB 오류까지 503 으로 둔갑한다
        assertThatThrownBy(() -> filterWith(new IllegalStateException("boom")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void a_committed_response_is_not_rewritten() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/request/list"), response,
                (req, res) -> {
                    ((MockHttpServletResponse) res).setCommitted(true);
                    throw new RedisConnectionFailureException("down");
                });

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
