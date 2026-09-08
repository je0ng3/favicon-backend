package com.capstone.favicon.config;

import com.capstone.favicon.security.BearerHttpSessionIdResolver;
import com.capstone.favicon.security.RestAccessDeniedHandler;
import com.capstone.favicon.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    private static final String[] PUBLIC_ENDPOINTS = {
            // 사용자
            "/users/auth/**",
            // 공지사항/FAQ
            "/notice/list", "/notice/view/*", "/faq/list",
            // 데이터셋
            "/data-set/filter", "/data-set/count", "/data-set/ratio", "/data-set/incrementDownload/*",
            "/data-set/top9", "/data-set/theme", "/data-set/*", "/data-set/category/*",
            "/data-set/stats", "/data-set/search-sorted", "/data-set/search-sorted/*", "/data-set/download/*",
            "/data-set/group-by-theme", "/data-set",
            // 기타
            "/analysis", "/trend/**", "/region",
            // 배포 헬스체크 (show-details=never 라 상태값만 노출)
            "/actuator/health"
    };
    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/statistics/**",
            "/s3/upload",
            "/s3/delete/*",
            "/faq/create",
            "/faq/*",
            "/notice/create",
            "/notice/*",
            // 심사는 S3 파일 이동·삭제를 일으키므로 관리자만 호출할 수 있어야 한다
            "/request/list/*/review",
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 세션 ID 운반 방식. 쿠키로 바꾸려면 CookieHttpSessionIdResolver 로 교체 + CSRF 활성화. */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new BearerHttpSessionIdResolver();
    }

    /** 로그인 시 SessionAuthenticator 가 같은 저장소에 쓰도록 명시적으로 노출한다. */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 세션 ID 를 쿠키가 아닌 Authorization 헤더로 받으므로 CSRF 대상이 아니다
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository()))
                // 저장 안 하면 401 마다 리다이렉트용 세션이 새로 만들어져 Redis 에 쌓인다
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                );
        return http.build();
    }
}
