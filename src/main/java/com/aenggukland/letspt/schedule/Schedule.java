package com.aenggukland.letspt.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Long trainerId;
    private Long memberId;
    private Long scheduleId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String classContent;
    private ScheduleStatus state;
    private String memo;
}
