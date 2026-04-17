package com.aenggukland.letspt.fcm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// FCM 토큰 관리 REST API 컨트롤러
// 앱 실행 시 기기 토큰 등록, 로그아웃 시 토큰 삭제를 처리한다
// 모든 엔드포인트는 JWT 인증이 필요하다
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    // FCM 토큰 등록/갱신: 앱 실행 시 기기 토큰을 서버에 등록한다 (이미 존재하면 갱신)
    @PostMapping
    public ResponseEntity<Void> saveFcmToken(@RequestAttribute("username") String username, @RequestBody @Valid FcmSaveRequest fcmSaveRequest){
        fcmTokenService.saveFcmToken(username, fcmSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // FCM 토큰 삭제: 로그아웃 또는 알림 해제 시 해당 기기의 토큰을 제거한다
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteFcmToken(@RequestAttribute("username") String username, @PathVariable String deviceId){
        fcmTokenService.deleteFcmToken(username, deviceId);
        return ResponseEntity.ok().build();
    }
}
