package com.aenggukland.letspt.fcm;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FcmSaveRequest {
    @NotBlank
    private String deviceId;
    @NotBlank
    private String token;
}
