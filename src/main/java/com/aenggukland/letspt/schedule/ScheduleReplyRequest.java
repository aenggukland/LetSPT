package com.aenggukland.letspt.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScheduleReplyRequest {

    // 예약 상태값
    @NotNull
    private ScheduleReplyState scheduleReplyState;

    // 메모
    private String memo;
}
