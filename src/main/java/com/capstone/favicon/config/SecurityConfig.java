package com.capstone.favicon.config;

import com.capstone.favicon.security.JwtAccessDeniedHandler;
import com.capstone.favicon.security.JwtAuthenticationEntryPoint;
import com.capstone.favicon.security.JwtAuthenticationFilter;
import com.capstone.favicon.security.JwtExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

    private static final String[] PUBLIC_ENDPOINTS = {
            // 사용자/통계
            "/users/auth/**", "/statistics/**",
            // 공지사항/FAQ
            "/notice/list", "/notice/view/*", "/faq/list",
            // 데이터셋
            "/data-set/filter", "/data-set/count", "/data-set/ratio", "/data-set/incrementDownload/*",
            "/data-set/top9", "/data-set/theme", "/data-set/*", "/data-set/category/*",
            "/data-set/stats", "/data-set/search-sorted", "/data-set/search-sorted/*", "/data-set/download/*",
            "/data-set/group-by-theme", "/data-set",
            // 기타
            "/analysis", "/trend/**", "/region"
    };
    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/s3/upload",
            "/s3/delete/*",
            "/faq/create",
            "/faq/*",
            "/notice/create",
            "/notice/*",
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionHandlerFilter, JwtAuthenticationFilter.class);
        return http.build();
    }
}
