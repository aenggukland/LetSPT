package com.aenggukland.letspt.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// JWT 인증 필터: 매 요청마다 한 번씩 실행(OncePerRequestFilter)되어 토큰을 검증한다
// Authorization 헤더(Bearer) 또는 accessToken 쿠키 두 경로에서 토큰을 추출한다
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String,String> redisTemplate;

    // 요청에서 JWT를 추출해 검증하고, 유효한 경우 SecurityContext에 인증 정보를 설정한다
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        // 1순위: Authorization 헤더의 Bearer 토큰 추출
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else if (request.getCookies() != null) {
            // 2순위: accessToken 쿠키에서 토큰 추출 (Thymeleaf 페이지 지원)
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && jwtProvider.validateToken(token)) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 반환
                return;
            }

            Claims claims = jwtProvider.parseToken(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // 컨트롤러에서 @RequestAttribute("username")로 접근할 수 있도록 설정
            request.setAttribute("username", username);

            // SecurityContext에 인증 정보 등록: Spring Security 권한 체계와 연동
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
