package com.aenggukland.letspt.scheduler;

import com.aenggukland.letspt.fcm.FcmTokenMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 주기적으로 실행되는 스케줄러
// @EnableScheduling은 LetSptApplication에 선언되어 있다
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final FcmTokenMapper fcmTokenMapper;

    // FCM 토큰 만료 처리: 매일 자정에 만료된 토큰의 is_expired 플래그를 true로 변경한다
    // 만료 토큰은 sendPush() 조회 시 제외되어 불필요한 FCM 요청을 줄인다
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void expireFcmTokens() {
        fcmTokenMapper.updateExpiredToken();
    }
}
