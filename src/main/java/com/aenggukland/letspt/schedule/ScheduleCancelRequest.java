package com.aenggukland.letspt.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ScheduleCancelRequest {
    // 취소 사요
    @NotBlank
    private String memo;
}
