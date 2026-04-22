package com.aenggukland.letspt.fcm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FCM", description = "Firebase Cloud Messaging 토큰 관리 API (JWT 인증 필수)")
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @Operation(summary = "FCM 토큰 등록/갱신", description = "앱 실행 시 기기 토큰을 서버에 등록합니다. 동일 deviceId가 이미 존재하면 갱신합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록/갱신 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping
    public ResponseEntity<Void> saveFcmToken(
            @RequestAttribute("username") String username,
            @RequestBody @Valid FcmSaveRequest fcmSaveRequest){
        fcmTokenService.saveFcmToken(username, fcmSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "FCM 토큰 삭제", description = "로그아웃 또는 알림 해제 시 해당 기기의 토큰을 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "토큰 없음")
    })
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteFcmToken(
            @RequestAttribute("username") String username,
            @Parameter(description = "기기 식별자") @PathVariable String deviceId){
        fcmTokenService.deleteFcmToken(username, deviceId);
        return ResponseEntity.ok().build();
    }
}
