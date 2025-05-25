package com.capstone.favicon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/**",
                                "/notice/create", "/notice/list", "/notice/{noticeId}", "/notice/view/{noticeId}", "/faq/create", "faq/{faqId}",
                                "/data-set/filter", "/data-set/count","/data-set/ratio", "/data-set/incrementDownload/{datasetId}", "/data-set/top10",
                                "/data-set/theme", "/data-set/{datasetId}", "/data-set/category/{themeId}", "/data-set/filter", "/faq/list", "faq/{faqId}",
                                "/users/login", "/users/logout", "/users/admin-check", "/s3/upload", "/s3/delete/{resourceId}",
                                "/users/delete-account", "/users/session-check", "/data-set", "/request/list","/request/list/{requestId}/review", "/request/{requestId}","/request/question",
                                "/request/question/{questionId}",   "/request/answer", "/request/answer/{answerId}","/data-set/search-sorted", "/data-set/search-sorted/{category}",
                                "/trend/**", "data-set/group-by-theme", "/region", "/analysis",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/data-set/download/{datasetId}").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                )
                .addFilterBefore(new Utf8Filter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}