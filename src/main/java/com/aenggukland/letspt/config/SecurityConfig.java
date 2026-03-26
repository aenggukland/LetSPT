package com.aenggukland.letspt.config;

import com.aenggukland.letspt.security.JwtFilter;
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

// Spring Security 설정: JWT 기반 Stateless 인증 구성
// CSRF 비활성화(JWT 토큰 인증 방식이므로 불필요, TODO S7), 세션 미사용, 경로별 권한 설정을 담당한다
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 보안 필터 체인 설정: 경로별 접근 권한과 JWT 필터 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // JWT 사용으로 CSRF 토큰 불필요 (TODO S7)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/register").permitAll()           // 인증 페이지
                    .requestMatchers("/api/auth/register", "/api/auth/login",
                            "/api/auth/refresh", "/api/auth/logout").permitAll()       // 인증 API
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger UI
                    .requestMatchers("/member/**").hasRole("MEMBER")
                    .requestMatchers("/admin/**").hasAnyRole("TRAINER", "MASTER")
                    .requestMatchers("/master/**").hasRole("MASTER")
                    .requestMatchers("/api/admin/**").hasAnyRole("TRAINER", "MASTER")
                    .requestMatchers("/api/master/**").hasRole("MASTER")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 앞에 등록

        return http.build();
    }

    // 비밀번호 인코더: BCrypt 알고리즘 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
