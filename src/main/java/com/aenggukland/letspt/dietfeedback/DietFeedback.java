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
public class DietFeedback {
    private Long feedbackId;
    private Long boardId;
    private Long trainerId;
    private Long memberId;
    private DietFeedbackType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
