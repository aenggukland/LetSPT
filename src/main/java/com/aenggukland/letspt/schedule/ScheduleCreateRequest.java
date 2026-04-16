package com.aenggukland.letspt.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

// 일정 생성 요청 DTO
@Getter
public class ScheduleCreateRequest {
    private Long scheduleId;

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @NotBlank
    private String classContent;
}
