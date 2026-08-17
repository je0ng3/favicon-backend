package com.capstone.favicon.config;

import com.capstone.favicon.FaviconApplication;
import com.capstone.favicon.aws.S3MetadataSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig 의 PUBLIC/ADMIN 목록은 문자열 배열이라 오타나 순서 변경이 컴파일에 걸리지 않는다.
 * 공개해야 할 경로가 닫히거나 관리자 전용 경로가 열리는 회귀를 여기서 잡는다.
 */
@SpringBootTest(classes = FaviconApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "ACTIVE=test",
        "JPA_DDL=create-drop",
        "spring.datasource.url=jdbc:h2:mem:accessdb;MODE=PostgreSQL;NON_KEYWORDS=VIEW,RANK;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        // /actuator/health 를 호출하므로 실물이 없는 지표는 꺼서 불필요한 접속 시도를 막는다
        "management.health.db.enabled=false",
        "management.health.redis.enabled=false",
        "REDIS_HOST=localhost",
        "SPRING_MAIL_USERNAME=test@example.com",
        "SPRING_MAIL_PASSWORD=test",
        "AWS_S3_BUCKET=test-bucket",
        "AWS_S3_REGION=ap-northeast-2",
        "AWS_S3_ACCESS_KEY_ID=test",
        "AWS_S3_SECRET_ACCESS_KEY=test",
        "API_KEY=test",
        "JWT_SECRET=test-jwt-secret-value-for-security-endpoint-access-test",
        "ADMIN_MAILS=admin@example.com"
})
class SecurityEndpointAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3MetadataSyncService s3MetadataSyncService;

    /** 인가 결과만 본다. 핸들러가 200 을 주는지 500 을 주는지는 여기서 볼 대상이 아니다. */
    private int statusOf(RequestBuilder request) throws Exception {
        return mockMvc.perform(request).andReturn().getResponse().getStatus();
    }

    @Test
    void publicEndpointsAreReachableWithoutToken() throws Exception {
        List<String> publicPaths = List.of(
                "/users/auth/login", "/notice/list", "/faq/list",
                "/region", "/data-set/top9", "/data-set/count", "/actuator/health");

        for (String path : publicPaths) {
            assertThat(statusOf(get(path))).describedAs(path).isNotIn(401, 403);
        }
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/users/scrap")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/request/list")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsRejectAnonymousCallers() throws Exception {
        mockMvc.perform(get("/statistics/user-stats")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/admin/delete-user").param("userId", "1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpointsAreForbiddenForNormalUsers() throws Exception {
        mockMvc.perform(get("/statistics/user-stats")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/admin/delete-user").param("userId", "1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpointsAreOpenToAdmins() throws Exception {
        assertThat(statusOf(get("/statistics/user-stats"))).isNotIn(401, 403);
        assertThat(statusOf(delete("/admin/delete-user").param("userId", "1"))).isNotIn(401, 403);
    }

    @Test
    void publicListPathWinsOverTheAdminWildcard() throws Exception {
        // PUBLIC 이 먼저 매칭되므로 /notice/list 는 공개, 나머지 /notice/* 는 관리자 전용이다
        assertThat(statusOf(get("/notice/list"))).isNotIn(401, 403);
        mockMvc.perform(get("/notice/999")).andExpect(status().isUnauthorized());
    }
}
