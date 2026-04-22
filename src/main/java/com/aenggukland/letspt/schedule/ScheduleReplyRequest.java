package com.aenggukland.letspt.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "일정 수락/거절 요청")
@Getter
public class ScheduleReplyRequest {

    @Schema(description = "응답 상태 (ACCEPTED / REJECTED)", example = "ACCEPTED")
    @NotNull
    private ScheduleReplyState scheduleReplyState;

    @Schema(description = "메모", example = "확인했습니다.")
    private String memo;
}
