package com.aenggukland.letspt.dietfeedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietFeedbackResponse {
    private Long feedbackId;
    private Long boardId;
    private Long trainerId;
    private DietFeedbackType type;
    private LocalDateTime createdAt;
}
