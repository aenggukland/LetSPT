package com.aenggukland.letspt.error;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorLog {

    private Long logId;
    private String errorCode;
    private String method;
    private String url;
    private String username;
    private String message;
    private String stackTrace;
    private LocalDateTime createdAt;
}
