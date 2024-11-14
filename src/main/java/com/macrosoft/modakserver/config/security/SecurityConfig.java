package com.macrosoft.modakserver.config.security;

import com.macrosoft.modakserver.config.jwt.JwtFilter;
import com.macrosoft.modakserver.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 상태 관리 (세션) 없음
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", // Swagger docs
                        "/api/test/", // test API
                        "/api/auth/refresh-access-token", "/api/auth/{}/login").permitAll() // 인증 필요없는 API
                .requestMatchers("/api/**").authenticated() // 인증 요구
                .requestMatchers("/api/campfires/**").authenticated() // 인증 요구
                .requestMatchers("/api/files/**").authenticated() // 인증 요구
                .anyRequest().denyAll() // 나머지는 거부
        );

        return http.build();
    }
}