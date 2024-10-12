package com.macrosoft.modakserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((auth) -> auth.disable()) ;// csrf 비활성화
        http
                .formLogin((auth) -> auth.disable()) ;// form 로그인 방식 비활성화
        http
                .httpBasic((auth) -> auth.disable()) ;// http basic 인증 비활성화
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.GET
                                ,"/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .anyRequest().authenticated()
                ) ;// 경로별 인가 작업
        return http.build();
    }
}