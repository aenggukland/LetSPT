package com.aenggukland.letspt.security;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// Refresh Token 엔티티: refresh_token 테이블과 1:1 매핑된다
// username당 1개만 허용되어 동일 계정 재로그인 시 기존 토큰이 덮어쓰인다 (TODO D2)
@Getter
@Builder
public class RefreshToken {

    private Long tokenId;
    private String username;  // refresh_token.username은 UNIQUE 제약 → 기기 1개만 로그인 허용
    private String token;     // UUID 형태의 랜덤 토큰 값
    private LocalDateTime expiresAt;  // 만료 시각: 로그인 시 현재 시각 + refreshExpirationMs
    private LocalDateTime createdAt;
}
