package com.aenggukland.letspt.fcm;

import com.aenggukland.letspt.board.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken {
    private Long fcmTokenId;
    private Long memberId;
    private String deviceId;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Boolean isExpired;
}

