package com.aenggukland.letspt.schedule;

import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper {

    void reservation(Long trainerId, @Valid ScheduleCreateRequest scheduleCreateRequest);

    void replyReservation(Schedule schedule);
}
