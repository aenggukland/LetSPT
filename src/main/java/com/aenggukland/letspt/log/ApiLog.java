package com.aenggukland.letspt.log;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiLog {

    private Long logId;
    private String username;
    private String method;
    private String url;
    private Integer status;
    private Long durationMs;
    private LocalDateTime createdAt;
}
