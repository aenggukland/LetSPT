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

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenMapper fcmTokenMapper;
    private final MemberMapper memberMapper;

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
            fcmTokenMapper.updateToken(fcmToken);
        } else {
            fcmTokenMapper.insertToken(fcmToken);
        }
    }

    public void deleteFcmToken(String username, String deviceId){
        Member member = getMemberInfo(username);
        int tokenCnt = fcmTokenMapper.getFcmTokenCnt(member.getMemberId(), deviceId);
        if(tokenCnt > 0){
            fcmTokenMapper.deleteToken(member.getMemberId(), deviceId);
        } else {
            throw new BusinessException(ErrorCode.DELETE_FCM_TOKEN_NOT_FOUND);
        }
    }

    public void sendPush(Long memberId, String type, String body) {
        List<FcmToken> fcmTokenList = fcmTokenMapper.getFcmTokenList(memberId);
        if(!fcmTokenList.isEmpty()) {
            for(FcmToken fcmToken : fcmTokenList){
                try {
                    Message message = Message.builder()
                            .setToken(fcmToken.getToken())
                            .setNotification(Notification.builder()
                                    .setTitle(type)
                                    .setBody(body)
                                    .build())
                            .build();
                    FirebaseMessaging.getInstance().send(message);
                } catch (FirebaseMessagingException e) {
                    log.warn("FCM 푸시 전송 실패 memberId: {}, token: {}", memberId, fcmToken.getToken());
                }
            }
        }
    }

    public Member getMemberInfo(String username){
        return memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
