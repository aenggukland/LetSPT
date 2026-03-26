package com.aenggukland.letspt.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT Access Token 생성·파싱·검증을 담당하는 컴포넌트
// HS256 알고리즘을 사용하며, 토큰 페이로드에 username과 role을 포함한다
@Component
public class JwtProvider {

    private final Key key;
    private final long expirationMs; // Access Token 유효기간 (밀리초)

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        // HS256 최소 키 길이(32바이트) 검증: 미달 시 앱 기동 실패로 잘못된 설정을 즉시 인지
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 32바이트 이상이어야 합니다. 현재: " + secret.getBytes().length + "바이트");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // Access Token 생성: subject=username, claim=role, 만료시간 설정 후 HS256으로 서명한다
    public String createToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 파싱: 서명을 검증하고 Claims(페이로드)를 반환한다
    // 서명 불일치·만료 등 이상이 있으면 JwtException을 던진다
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 유효성 검사: 파싱 성공 여부로 유효성을 판단하며 예외는 false로 변환한다
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
