package com.aenggukland.letspt.schedule;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 일정관리 비즈니스 로직을 처리하는 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final MemberMapper memberMapper;

    // 트레이너 -> 사용자 일정 확인 요청
    public void reservation(String username, ScheduleCreateRequest scheduleCreateRequest) {
        Member trainerInfo = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if(!trainerInfo.getRoleId().equals(MemberRole.TRAINER.getRoleId())){
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }
        Schedule schedule = Schedule.builder()
                .startDateTime(scheduleCreateRequest.getStartDateTime())
                .endDateTime(scheduleCreateRequest.getEndDateTime())
                .build();
        int trainerPtCnt = scheduleMapper.getTrainerPtCnt(trainerInfo.getMemberId(), schedule);
        if(trainerPtCnt > 0){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_PT_DUPLICATION);
        }

        if(memberMapper.findById(scheduleCreateRequest.getMemberId()).isEmpty()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        scheduleMapper.reservation(trainerInfo.getMemberId(), scheduleCreateRequest);
    }

    // 회원 -> 트레이너 일정 요청 수락/거절
    public void replyReservation(String username, Long scheduleId, ScheduleReplyRequest scheduleReplyRequest) {
        if(scheduleReplyRequest.getScheduleReplyState() == ScheduleReplyState.MEMBER_CANCEL && (scheduleReplyRequest.getMemo() == null ||scheduleReplyRequest.getMemo().isBlank())){
            throw new BusinessException(ErrorCode.SCHEDULE_CANCEL_MEMO_REQUIRED);
        }
        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Schedule checkSchedule = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        if(!member.getMemberId().equals(checkSchedule.getMemberId())){
            throw new BusinessException(ErrorCode.SCHEDULE_MEMBER_MISMATCH);
        }

        Schedule schedule = Schedule.builder()
                .memberId(member.getMemberId())
                .scheduleId(scheduleId)
                .state(ScheduleState.valueOf(scheduleReplyRequest.getScheduleReplyState().name()))
                .memo(scheduleReplyRequest.getMemo())
                .build();

        scheduleMapper.replyReservation(schedule);
    }

    // 트레이너가 예약 내용 수정(예약 요청 상태인 수업만 가능)
    public void updateReservation(String username, Long scheduleId, ScheduleUpdateRequest scheduleUpdateRequest) {
        Schedule schedule = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        if(schedule.getState() != ScheduleState.RESERVATION){
            throw new BusinessException(ErrorCode.SCHEDULE_UPDATE_DENIED);
        }

        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if(!member.getMemberId().equals(schedule.getTrainerId())){
            throw new BusinessException(ErrorCode.SCHEDULE_MEMBER_MISMATCH);
        }

        Schedule updateSchedule = Schedule.builder()
                .scheduleId(scheduleId)
                .startDateTime(scheduleUpdateRequest.getStartDateTime())
                .endDateTime(scheduleUpdateRequest.getEndDateTime())
                .classContent(scheduleUpdateRequest.getClassContent())
                .build();

        int trainerPtCnt = scheduleMapper.getTrainerPtCnt(member.getMemberId(), updateSchedule);
        if(trainerPtCnt > 0){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_PT_DUPLICATION);
        }

        int updatedCount = scheduleMapper.updateReservation(updateSchedule);
        if(updatedCount == 0){
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_MODIFIABLE);
        }
    }
}
