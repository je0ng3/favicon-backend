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
                        .requestMatchers("/users/email-check", "/users/code-check", "users/register",
                                "/notice/create", "/notice/list", "/notice/{noticeId}", "/notice/view/{noticeId}", "/faq/create", "faq/{faqId}",
                                "/users/login", "/users/logout", "/users/admin-check",
                                "/users/delete-account", "/users/session-check",
                                "/data-set/search-sorted", "/data-set/search-sorted/{category}",
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
