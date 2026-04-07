package com.aenggukland.letspt.schedule;

import com.aenggukland.letspt.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListResponse {
    private MemberRole memberRole;
    private List<ScheduleResponse> schedules;
}
