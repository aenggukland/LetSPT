package com.aenggukland.letspt.security;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RefreshToken {

    private Long tokenId;
    private String username;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
