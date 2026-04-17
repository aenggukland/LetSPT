package com.aenggukland.letspt.fcm;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// FCM(Firebase Cloud Messaging) 토큰 관리 및 푸시 알림 전송 서비스
// 기기별 토큰 등록/수정/삭제와 실제 푸시 발송을 담당한다
@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenMapper fcmTokenMapper;
    private final MemberMapper memberMapper;

    // FCM 토큰 저장: 같은 기기(deviceId)의 토큰이 이미 존재하면 갱신, 없으면 신규 등록한다
    // 토큰 유효기간은 60일로 설정한다
    public void saveFcmToken(String username, FcmSaveRequest fcmSaveRequest){
        Member member = getMemberInfo(username);
        FcmToken fcmToken = FcmToken.builder()
                .memberId(member.getMemberId())
                .token(fcmSaveRequest.getToken())
                .deviceId(fcmSaveRequest.getDeviceId())
                .expiredAt(LocalDateTime.now().plusDays(60))
                .build();

        int tokenCnt = fcmTokenMapper.getFcmTokenCnt(member.getMemberId(), fcmSaveRequest.getDeviceId());
        if(tokenCnt > 0){
            fcmTokenMapper.updateToken(fcmToken); // 기존 토큰 갱신
        } else {
            fcmTokenMapper.insertToken(fcmToken); // 신규 토큰 등록
        }
    }

    // FCM 토큰 삭제: 로그아웃 또는 기기 변경 시 해당 기기의 토큰을 제거한다
    public void deleteFcmToken(String username, String deviceId){
        Member member = getMemberInfo(username);
        int tokenCnt = fcmTokenMapper.getFcmTokenCnt(member.getMemberId(), deviceId);
        if(tokenCnt > 0){
            fcmTokenMapper.deleteToken(member.getMemberId(), deviceId);
        } else {
            throw new BusinessException(ErrorCode.DELETE_FCM_TOKEN_NOT_FOUND);
        }
    }

    // 푸시 알림 전송: 대상 회원의 만료되지 않은 모든 기기 토큰에 FCM 메시지를 발송한다
    // 전송 실패 시 예외를 던지지 않고 경고 로그만 남겨 다른 기기 전송에 영향을 주지 않는다
    public void sendPush(Long memberId, FcmType type, String body, Long targetId) {
        List<FcmToken> fcmTokenList = fcmTokenMapper.getFcmTokenList(memberId);
        if(!fcmTokenList.isEmpty()) {
            for(FcmToken fcmToken : fcmTokenList){
                try {
                    Message message = Message.builder()
                            .setToken(fcmToken.getToken())
                            .setNotification(Notification.builder()
                                    .setTitle(type.getTitle())
                                    .setBody(body)
                                    .build())
                            .putData("type", type.name())           // 클라이언트에서 알림 종류 분기 처리용
                            .putData("targetId", String.valueOf(targetId)) // 알림 클릭 시 이동할 대상 ID
                            .build();
                    FirebaseMessaging.getInstance().send(message);
                } catch (FirebaseMessagingException e) {
                    log.warn("FCM 푸시 전송 실패 memberId: {}, token: {}", memberId, fcmToken.getToken());
                }
            }
        }
    }

    // username으로 회원 정보를 조회하는 내부 헬퍼 메서드
    public Member getMemberInfo(String username){
        return memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
