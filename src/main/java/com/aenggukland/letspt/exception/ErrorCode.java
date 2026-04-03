package com.aenggukland.letspt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 도메인별 에러 코드 정의: HTTP 상태 코드 + 사용자에게 반환할 한국어 메시지를 관리한다
// BusinessException 생성 시 이 값을 사용하며, GlobalExceptionHandler가 응답으로 변환한다
@Getter
public enum ErrorCode {

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호와 동일합니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다. 다시 로그인해주세요."),

    // File
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),
    UNAUTHORIZED_BOARD_WRITE(HttpStatus.FORBIDDEN, "해당 카테고리에 글을 작성할 권한이 없습니다."),
    INVALID_BOARD_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 게시글 카테고리입니다."),
    
    // Schedule
    SCHEDULE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "일정 생성에 대한 권한이 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "일정이 존재하지 않습니다."),
    SCHEDULE_MEMBER_MISMATCH(HttpStatus.FORBIDDEN, "본인의 일정만 접근할 수 있습니다."),
    SCHEDULE_TRAINER_PT_DUPLICATION(HttpStatus.FORBIDDEN, "같은 시간대에 PT 일정이 있습니다."),
    SCHEDULE_CANCEL_MEMO_REQUIRED(HttpStatus.FORBIDDEN, "예약 취소 시 필수로 이유를 작성해야합니다."),
    SCHEDULE_UPDATE_DENIED(HttpStatus.FORBIDDEN, "예약 요청 상태의 수업만 수정이 가능합니다."),
    SCHEDULE_NOT_MODIFIABLE(HttpStatus.BAD_REQUEST, "수정 가능한 일정이 없습니다."),

    // Rate Limit
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // Server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // Common
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 값을 읽을 수 없습니다.");

    private final HttpStatus status;  // GlobalExceptionHandler가 HTTP 응답 상태로 사용
    private final String message;     // 클라이언트에 노출되는 한국어 오류 메시지

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
