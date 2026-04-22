package com.aenggukland.letspt.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "FCM 토큰 등록 요청")
@Getter
public class FcmSaveRequest {

    @Schema(description = "기기 고유 식별자", example = "device_abc123")
    @NotBlank
    private String deviceId;

    @Schema(description = "FCM 기기 토큰", example = "fcm_token_xyz...")
    @NotBlank
    private String token;
}
