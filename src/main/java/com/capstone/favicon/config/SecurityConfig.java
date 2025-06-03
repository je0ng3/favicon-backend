package com.capstone.favicon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
                .cors(Customizer.withDefaults())
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/**", "/statistics/**", "/admin/**", "/gpt/chat",
                                "/notice/create", "/notice/list", "/notice/{noticeId}", "/notice/view/{noticeId}", "/faq/create", "/faq/{faqId}",
                                "/data-set/filter", "/data-set/count","/data-set/ratio", "/data-set/incrementDownload/{datasetId}", "/data-set/top9",
                                "/data-set/theme", "/data-set/{datasetId}", "/data-set/category/{themeId}", "/data-set/filter", "/faq/list", "/faq/{faqId}",
                                "/s3/upload", "/s3/delete/{resourceId}", "/analysis", "/data-set/stats", "/request/stats", "/request/download/{requestId}",
                                "/data-set", "/request/list","/request/list/{requestId}/review", "/request/{requestId}","/request/question",
                                "/request/question/{questionId}",	"/request/answer", "/request/answer/{answerId}","/trend/**", "/data-set/search-sorted", "/data-set/search-sorted/{category}",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/data-set/download/{datasetId}", "/data-set/group-by-theme", "/region").permitAll()
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
