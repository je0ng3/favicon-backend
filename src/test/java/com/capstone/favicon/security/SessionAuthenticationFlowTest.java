package com.capstone.favicon.security;

import com.capstone.favicon.FaviconApplication;
import com.capstone.favicon.aws.S3MetadataSyncService;
import com.capstone.favicon.user.application.service.RedisService;
import com.capstone.favicon.user.domain.User;
import com.capstone.favicon.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 로그인 응답의 세션 ID 로 실제 인증이 되고 로그아웃으로 끊기는지 확인한다.
 * Redis 대신 인메모리 저장소를 끼워, 헤더 → SessionRepositoryFilter → SecurityContext 경로 전체를 태운다.
 */
@SpringBootTest(classes = FaviconApplication.class)
@AutoConfigureMockMvc
@Import(SessionAuthenticationFlowTest.InMemorySessionConfig.class)
@TestPropertySource(properties = {
        "ACTIVE=test",
        "JPA_DDL=create-drop",
        "spring.datasource.url=jdbc:h2:mem:sessionflowdb;MODE=PostgreSQL;NON_KEYWORDS=VIEW,RANK;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "management.health.db.enabled=false",
        "management.health.redis.enabled=false",
        "REDIS_HOST=localhost",
        // Redis 저장소 대신 아래 InMemorySessionConfig 를 쓴다
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.session.SessionAutoConfiguration",
        "SPRING_MAIL_USERNAME=test@example.com",
        "SPRING_MAIL_PASSWORD=test",
        "AWS_S3_BUCKET=test-bucket",
        "AWS_S3_REGION=ap-northeast-2",
        "AWS_S3_ACCESS_KEY_ID=test",
        "AWS_S3_SECRET_ACCESS_KEY=test",
        "API_KEY=test",
        "ADMIN_MAILS=admin@example.com"
})
class SessionAuthenticationFlowTest {

    @TestConfiguration
    @EnableSpringHttpSession
    static class InMemorySessionConfig {
        @Bean
        IndexedMapSessionRepository sessionRepository() {
            return new IndexedMapSessionRepository();
        }
    }

    /**
     * 운영은 principal 인덱스를 지원하는 RedisIndexedSessionRepository 를 쓴다.
     * 인덱스 없는 MapSessionRepository 로 두면 UserSessionRegistry 가 통째로 no-op 이 되어
     * 강제 로그아웃이 안 돼도 테스트가 통과해버린다.
     */
    static class IndexedMapSessionRepository
            implements SessionRepository<MapSession>, FindByIndexNameSessionRepository<MapSession> {

        private final Map<String, Session> sessions = new ConcurrentHashMap<>();
        private final MapSessionRepository delegate = new MapSessionRepository(sessions);

        @Override
        public MapSession createSession() {
            return delegate.createSession();
        }

        @Override
        public void save(MapSession session) {
            delegate.save(session);
        }

        @Override
        public MapSession findById(String id) {
            return delegate.findById(id);
        }

        @Override
        public void deleteById(String id) {
            delegate.deleteById(id);
        }

        @Override
        public Map<String, MapSession> findByIndexNameAndIndexValue(String indexName, String indexValue) {
            if (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName)) {
                return Collections.emptyMap();
            }
            Map<String, MapSession> found = new LinkedHashMap<>();
            sessions.forEach((id, session) -> {
                if (indexValue.equals(principalOf(session))) {
                    found.put(id, (MapSession) session);
                }
            });
            return found;
        }

        private String principalOf(Session session) {
            SecurityContext context = session.getAttribute("SPRING_SECURITY_CONTEXT");
            return context == null || context.getAuthentication() == null
                    ? null : context.getAuthentication().getName();
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IndexedMapSessionRepository sessionRepository;

    @MockBean
    private S3MetadataSyncService s3MetadataSyncService;
    @MockBean
    private RedisService redisService;

    private static final String PROTECTED_PATH = "/request/list";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("member@test.com");
        user.setUsername("member");
        user.setPassword(passwordEncoder.encode("raw-pw"));
        user.setRole(0);
        userRepository.save(user);
    }

    private String login() throws Exception {
        String body = mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"member@test.com\",\"password\":\"raw-pw\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode token = objectMapper.readTree(body).path("data").path("token");
        assertThat(token.asText()).isNotBlank();
        return token.asText();
    }

    @Test
    void the_login_session_id_authenticates_later_requests() throws Exception {
        String sessionId = login();

        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void requests_without_a_valid_session_id_are_rejected() throws Exception {
        mockMvc.perform(get(PROTECTED_PATH)).andExpect(status().isUnauthorized());
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer not-a-session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_makes_the_session_id_unusable() throws Exception {
        String sessionId = login();

        mockMvc.perform(post("/users/auth/logout").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk());

        // JWT 와 달리 서버가 즉시 끊을 수 있어야 한다. 이게 이 방식으로 바꾼 이유다
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_issues_a_new_session_id_each_time() throws Exception {
        assertThat(login()).isNotEqualTo(login());
    }

    @Test
    void the_session_is_indexed_under_the_email() throws Exception {
        Session session = sessionRepository.findById(login());
        SecurityContext context = session.getAttribute("SPRING_SECURITY_CONTEXT");

        // UserSessionRegistry 가 principal name 으로 세션을 찾으므로 이 값이 email 이어야 한다
        assertThat(context.getAuthentication().getName()).isEqualTo("member@test.com");
    }

    @Test
    void deleting_the_account_logs_out_every_device() throws Exception {
        String phone = login();
        String laptop = login();

        mockMvc.perform(delete("/users/auth/delete-account").header("Authorization", "Bearer " + phone))
                .andExpect(status().isOk());

        // 탈퇴한 계정의 principal 은 세션에서 그대로 복원되므로, 세션을 지우지 않으면 계속 인증된다
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + phone))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + laptop))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_returns_the_current_session_and_401_without_one() throws Exception {
        String sessionId = login();

        mockMvc.perform(post("/users/auth/refresh").header("Authorization", "Bearer " + sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(sessionId));

        mockMvc.perform(post("/users/auth/refresh")).andExpect(status().isUnauthorized());
    }

    @Test
    void a_wrong_password_is_401_and_issues_no_session() throws Exception {
        mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"member@test.com\",\"password\":\"wrong-pw\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void a_redis_failure_inside_a_handler_is_503_not_500() throws Exception {
        // 세션 조회는 필터라 RedisUnavailableFilter 가 잡지만, OTP 는 핸들러라 advice 가 잡는다
        org.mockito.Mockito.doThrow(new RedisConnectionFailureException("down"))
                .when(redisService).setCode(org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString());

        mockMvc.perform(post("/users/auth/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\"}"))
                .andExpect(status().isServiceUnavailable());
    }
}
