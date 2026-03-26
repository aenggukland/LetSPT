package com.aenggukland.letspt.exception;

import lombok.Getter;

// API 에러 응답 DTO: code(에러 코드명)와 message(사용자 메시지)로 구성된다
// GlobalExceptionHandler에서 모든 예외를 이 형태로 통일해 반환한다
@Getter
public class ErrorResponse {

    private final String code;    // ErrorCode enum 이름 또는 "VALIDATION_ERROR"
    private final String message; // 클라이언트에 노출되는 오류 설명

    // ErrorCode 기반 생성자: code는 enum 이름, message는 한국어 메시지
    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    // 직접 지정 생성자: Validation 오류 등 ErrorCode와 무관한 응답에 사용
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
