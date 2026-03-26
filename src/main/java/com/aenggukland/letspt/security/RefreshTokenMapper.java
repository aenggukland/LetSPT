package com.aenggukland.letspt.security;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

// Refresh Token 데이터 접근 Mapper: SQL은 RefreshTokenMapper.xml에만 작성한다
@Mapper
public interface RefreshTokenMapper {

    // Refresh Token 저장: username 중복 시 기존 토큰을 덮어쓴다 (UPSERT)
    void save(RefreshToken refreshToken);

    // 토큰 값으로 단건 조회
    Optional<RefreshToken> findByToken(String token);

    // username에 해당하는 토큰 삭제 (로그아웃·탈퇴·만료 시 호출)
    void deleteByUsername(String username);
}
