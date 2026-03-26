package com.aenggukland.letspt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 도메인 비즈니스 규칙 위반을 나타내는 단일 예외 클래스
// HTTP 상태 코드와 메시지는 ErrorCode에서 관리하며, GlobalExceptionHandler가 처리한다
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    // ErrorCode로 예외를 생성하며, 메시지는 ErrorCode의 message를 사용한다
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
