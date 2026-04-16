package com.aenggukland.letspt.scheduler;

import com.aenggukland.letspt.fcm.FcmTokenMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final FcmTokenMapper fcmTokenMapper;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void expireFcmTokens() {
        // 만료된 토큰 처리
        fcmTokenMapper.updateExpiredToken();
    }
}
