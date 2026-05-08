package com.aenggukland.letspt.dietfeedback;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DietFeedbackRequest {
    @NotNull(message = "피드백 타입은 필수입니다.")
    private DietFeedbackType type;
}
