package com.aenggukland.letspt.fcm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FcmTokenMapper {
    void insertToken(FcmToken fcmToken);
    void updateToken(FcmToken fcmToken);
    void deleteToken(Long memberId, String deviceId);
    List<FcmToken> getFcmTokenList(Long memberId);

    int getFcmTokenCnt(Long memberId, String deviceId);

    void updateExpiredToken();
}
