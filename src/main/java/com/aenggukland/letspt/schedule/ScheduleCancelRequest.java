package com.aenggukland.letspt.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "수업 취소 요청")
@Getter
public class ScheduleCancelRequest {

    @Schema(description = "취소 사유", example = "트레이너 개인 사정으로 인한 취소")
    @NotBlank
    private String memo;
}
