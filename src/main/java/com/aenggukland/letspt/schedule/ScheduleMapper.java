package com.aenggukland.letspt.schedule;

import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ScheduleMapper {

    void reservation(Long trainerId,@Param("request") ScheduleCreateRequest scheduleCreateRequest);

    void replyReservation(Schedule schedule);

    int getTrainerPtCnt(Long trainerId, @Param("request") Schedule schedule);

    Optional<Schedule> findByScheduleId(Long scheduleId);

    int updateReservation(Schedule scheduleUpdateRequest);

    int cancelReservation(Schedule scheduleCancelRequest);
}
