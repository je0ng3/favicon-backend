package com.capstone.favicon.config;

import com.capstone.favicon.FaviconApplication;
import com.capstone.favicon.aws.S3MetadataSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 배포 후 헬스체크는 토큰 없이 /actuator/health 를 호출한다. SecurityConfig 가
 * anyRequest().authenticated() 로 닫혀 있어서, PUBLIC_ENDPOINTS 에서 이 경로가 빠지는 순간
 * 헬스체크는 401 을 받고 모든 배포가 롤백된다. 그 회귀를 여기서 막는다.
 */
@SpringBootTest(
        classes = FaviconApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = {
        "ACTIVE=test",
        "JPA_DDL=create-drop",
        "spring.datasource.url=jdbc:h2:mem:healthdb;MODE=PostgreSQL;NON_KEYWORDS=VIEW,RANK;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        // 실물이 없는 외부 의존성 지표는 제외 — 여기서 검증할 대상이 아니다
        "management.health.db.enabled=false",
        "management.health.redis.enabled=false",
        // 애플리케이션이 요구하는 외부 설정 자리채움
        "REDIS_HOST=localhost",
        "SPRING_MAIL_USERNAME=test@example.com",
        "SPRING_MAIL_PASSWORD=test",
        "AWS_S3_BUCKET=test-bucket",
        "AWS_S3_REGION=ap-northeast-2",
        "AWS_S3_ACCESS_KEY_ID=test",
        "AWS_S3_SECRET_ACCESS_KEY=test",
        "API_KEY=test",
        "JWT_SECRET=test-jwt-secret-value-for-actuator-health-endpoint-test",
        "ADMIN_MAILS=admin@example.com"
})
class ActuatorHealthEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // @Scheduled 에 initialDelay 가 없어 컨텍스트가 뜨는 즉시 실물 S3 를 호출한다
    @MockBean
    private S3MetadataSyncService s3MetadataSyncService;

    @Test
    void healthEndpointIsReachableWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void healthEndpointDoesNotLeakComponentDetails() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        // show-details=never 이므로 응답에 개별 지표(diskSpace 등)가 실려서는 안 된다
        assertThat(response.getBody()).doesNotContain("components");
        assertThat(response.getBody()).doesNotContain("diskSpace");
    }

    @Test
    void otherActuatorEndpointsAreNotExposed() {
        // include=health 만 열었으므로 env/beans 같은 민감한 엔드포인트는 노출되면 안 된다
        assertThat(restTemplate.getForEntity("/actuator/env", String.class).getStatusCode())
                .isNotEqualTo(HttpStatus.OK);
        assertThat(restTemplate.getForEntity("/actuator/beans", String.class).getStatusCode())
                .isNotEqualTo(HttpStatus.OK);
    }
}
