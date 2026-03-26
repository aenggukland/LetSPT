package com.aenggukland.letspt.error;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 에러 로그 엔티티: error_log 테이블에 예외 발생 정보를 기록한다
// GlobalExceptionHandler에서 BusinessException과 미처리 예외 발생 시 저장된다
@Getter
@Builder
public class ErrorLog {

    private Long logId;
    private String errorCode;  // ErrorCode enum 이름
    private String method;     // HTTP 메서드 (GET, POST 등)
    private String url;        // 요청 URI
    private String username;   // 인증된 사용자명 (미인증 요청은 null)
    private String message;    // 예외 메시지
    private String stackTrace; // 스택트레이스 (INTERNAL_SERVER_ERROR에만 기록)
    private LocalDateTime createdAt;
}
