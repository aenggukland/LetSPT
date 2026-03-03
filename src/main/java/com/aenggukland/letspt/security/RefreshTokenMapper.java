package com.aenggukland.letspt.security;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUsername(String username);
}
