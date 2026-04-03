package com.aenggukland.letspt.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleUpdateRequest {
    // 수업 시작 날짜
    @NotNull
    private LocalDateTime startDateTime;
    // 수업 종료 날짜
    @NotNull
    private LocalDateTime endDateTime;
    // 수업내용
    private String classContent;
}
