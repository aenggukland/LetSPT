package com.aenggukland.letspt.config;

import com.aenggukland.letspt.error.ErrorLog;
import com.aenggukland.letspt.error.ErrorLogMapper;
import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;

// 전역 예외 처리 핸들러: 모든 컨트롤러에서 발생하는 예외를 중앙에서 처리한다
// 예외 발생 시 ErrorLogMapper로 DB에 로그를 저장하고 ErrorResponse를 반환한다
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogMapper errorLogMapper;

    // @Valid 검증 실패 처리: 필드별 오류 메시지를 모아 400 Bad Request로 반환한다
    // DB 로그를 저장하지 않는다 (입력값 오류이므로 시스템 이상이 아님)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    // 비즈니스 예외 처리: ErrorCode에 정의된 HTTP 상태와 메시지로 응답하고 DB에 로그를 남긴다
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e,
                                                                 HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        saveErrorLog(errorCode.name(), request, e.getMessage(), null); // 스택트레이스 불필요
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(errorCode));
    }

    // 미처리 예외 처리: 500 Internal Server Error로 응답하고 스택트레이스를 포함해 DB에 저장한다
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        saveErrorLog(ErrorCode.INTERNAL_SERVER_ERROR.name(), request, e.getMessage(), stackTrace(e));
        return ResponseEntity.status(500).body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 에러 로그 저장: 요청 메서드·URL·인증 사용자명을 함께 기록한다
    // 미인증 요청(anonymousUser)은 username을 null로 저장한다
    private void saveErrorLog(String errorCode, HttpServletRequest request,
                              String message, String stackTrace) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : null;

        errorLogMapper.save(ErrorLog.builder()
                .errorCode(errorCode)
                .method(request.getMethod())
                .url(request.getRequestURI())
                .username(username)
                .message(message)
                .stackTrace(stackTrace)
                .build());
    }

    // 예외의 스택트레이스를 문자열로 변환한다 (INTERNAL_SERVER_ERROR 로깅용)
    private String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
