package com.capston.favicon.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
                        .requestMatchers("/user/email-check", "/user/code-check", "user/register",
                                "/user/login", "/user/logout",
                                "/data-set/search-sorted", "/data-set/search-sorted/{category}",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/data-set/filter", "/data-set", "/data-set/top10", "/data-set/incrementDownload/{datasetId}", "/data-set/theme",
                                "/data-set/count", "/data-set/{datasetId}", "/data-set/ratio").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session
                        .maximumSessions(1)
                );

        return http.build();
    }
}
