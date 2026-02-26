package com.aenggukland.letspt.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // 테스트용 시크릿 (32바이트 이상)
        jwtProvider = new JwtProvider(
                "test-secret-key-for-junit-test-only!!", 
                86400000L
        );
    }

    @Test
    @DisplayName("토큰 생성 후 파싱하면 username과 role이 일치한다")
    void createAndParseToken() {
        String token = jwtProvider.createToken("user1", "MEMBER");

        Claims claims = jwtProvider.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("user1");
        assertThat(claims.get("role", String.class)).isEqualTo("MEMBER");
    }

    @Test
    @DisplayName("유효한 토큰은 validateToken이 true를 반환한다")
    void validateValidToken() {
        String token = jwtProvider.createToken("user1", "MEMBER");

        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("변조된 토큰은 validateToken이 false를 반환한다")
    void validateInvalidToken() {
        assertThat(jwtProvider.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 validateToken이 false를 반환한다")
    void validateExpiredToken() {
        JwtProvider shortLivedProvider = new JwtProvider(
                "test-secret-key-for-junit-test-only!!", 
                1L  // 1ms
        );
        String token = shortLivedProvider.createToken("user1", "MEMBER");

        // 만료 대기
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
