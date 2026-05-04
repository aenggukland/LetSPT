package com.aenggukland.letspt.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodayScheduleResult {
    private Long scheduleId;
    private Long trainerId;
    private Long memberId;
    private LocalDateTime startDateTime;
    private String classContent;
    private String trainerName;
    private String memberName;
}
