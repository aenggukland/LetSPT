package com.aenggukland.letspt.scheduler;

import com.aenggukland.letspt.common.CommonMethod;
import com.aenggukland.letspt.fcm.FcmTokenMapper;
import com.aenggukland.letspt.fcm.FcmTokenService;
import com.aenggukland.letspt.fcm.FcmType;
import com.aenggukland.letspt.schedule.ScheduleMapper;
import com.aenggukland.letspt.schedule.TodayScheduleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// 주기적으로 실행되는 스케줄러
// @EnableScheduling은 LetSptApplication에 선언되어 있다
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final FcmTokenMapper fcmTokenMapper;
    private final FcmTokenService fcmTokenService;
    private final ScheduleMapper scheduleMapper;

    // FCM 토큰 만료 처리: 매일 자정에 만료된 토큰의 is_expired 플래그를 true로 변경한다
    // 만료 토큰은 sendPush() 조회 시 제외되어 불필요한 FCM 요청을 줄인다
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void expireFcmTokens() {
        fcmTokenMapper.updateExpiredToken();
    }

    // 당일 수업 알림: 매일 오전 8시에 오늘 COMPLETE 상태인 수업의 회원·트레이너에게 FCM 푸시를 발송한다
    @Scheduled(cron = "0 0 8 * * *") // 매일 오전 8시
    public void notifyTodaySchedules() {
        List<TodayScheduleResult> todaySchedules = scheduleMapper.getTodaySchedules();
        for (TodayScheduleResult schedule : todaySchedules) {
            String startTime = CommonMethod.formatDateTime(schedule.getStartDateTime());
            fcmTokenService.sendPush(
                    schedule.getMemberId(),
                    FcmType.SCHEDULE_TODAY_REMINDER,
                    schedule.getTrainerName() + "님과 " + startTime + "에 " + schedule.getClassContent() + " 수업이 있습니다.",
                    schedule.getScheduleId()
            );
            fcmTokenService.sendPush(
                    schedule.getTrainerId(),
                    FcmType.SCHEDULE_TODAY_REMINDER,
                    schedule.getMemberName() + "님과 " + startTime + "에 " + schedule.getClassContent() + " 수업이 있습니다.",
                    schedule.getScheduleId()
            );
        }
    }
}
