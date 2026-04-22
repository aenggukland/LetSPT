package com.aenggukland.letspt.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "일정 예약 요청")
@Getter
public class ScheduleCreateRequest {
    @Schema(description = "일정 ID (수정 시 사용)", example = "1")
    private Long scheduleId;

    @Schema(description = "대상 회원 ID", example = "42")
    @NotNull
    private Long memberId;

    @Schema(description = "수업 시작 일시", example = "2026-05-01T10:00:00")
    @NotNull
    private LocalDateTime startDateTime;

    @Schema(description = "수업 종료 일시", example = "2026-05-01T11:00:00")
    @NotNull
    private LocalDateTime endDateTime;

    @Schema(description = "수업 내용", example = "하체 스쿼트 위주 트레이닝")
    @NotBlank
    private String classContent;
}
