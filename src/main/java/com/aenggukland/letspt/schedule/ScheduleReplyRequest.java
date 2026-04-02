package com.aenggukland.letspt.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ScheduleReplyRequest {

    // 예약 상태값
    @NotBlank
    private String scheduleStatus;

    // 메모
    private String memo;
}
