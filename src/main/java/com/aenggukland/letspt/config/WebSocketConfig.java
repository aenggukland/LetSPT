package com.aenggukland.letspt.config;

import com.aenggukland.letspt.chat.ChatWebSocketHandler;
import com.aenggukland.letspt.security.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// WebSocket 엔드포인트 등록 설정
// /ws/chat/{chatRoomId} 경로로 연결을 허용하며, CORS는 모든 오리진을 허용한다
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final CorsProperties corsProperties;

    // WebSocket 핸들러 등록: 채팅방별 URL 패턴으로 연결을 처리한다
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat/{chatRoomId}")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins(corsProperties.getAllowedOrigins().toArray(String[]::new));
    }
}