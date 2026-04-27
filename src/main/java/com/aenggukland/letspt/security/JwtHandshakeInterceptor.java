package com.aenggukland.letspt.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// WebSocket 핸드셰이크 단계에서 JWT를 검증하는 인터셉터
// HTTP Upgrade 요청 시 한 번 실행되며, 검증 통과 시 username을 세션 attributes에 저장한다
// JwtFilter와 동일한 추출 순서(쿠키 우선, Authorization 헤더 차선)를 사용한다
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // false 반환 시 Spring이 WebSocket 업그레이드를 거부(HTTP 403)한다
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String token = extractToken(servletRequest.getServletRequest());

        if (token == null || !jwtProvider.validateToken(token)) {
            return false;
        }

        try {
            // Redis 장애 시 fail-closed: 연결을 허용하지 않는다
            if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        Claims claims = jwtProvider.parseToken(token);
        attributes.put("username", claims.getSubject());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
