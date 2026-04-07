package com.aenggukland.letspt.schedule;

import com.aenggukland.letspt.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ScheduleResponse {
    private Long scheduleId;
    private String memberName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String classContent;
    private ScheduleState state;
}
