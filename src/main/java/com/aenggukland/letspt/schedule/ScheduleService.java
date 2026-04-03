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
    public void reservation(String username, @Valid ScheduleCreateRequest scheduleCreateRequest) {
        Member trainerInfo = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if(!trainerInfo.getRoleId().equals(MemberRole.TRAINER.getRoleId())){
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        int trainerPtCnt = scheduleMapper.getTrainerPtCnt(trainerInfo.getMemberId(), scheduleCreateRequest);
        if(trainerPtCnt > 0){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_PT_DUPLICATION);
        }

        if(memberMapper.findById(scheduleCreateRequest.getMemberId()).isEmpty()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        scheduleMapper.reservation(trainerInfo.getMemberId(), scheduleCreateRequest);
    }

    // 회원 -> 트레이너 일정 요청 수락/거절
    public void replyReservation(String username, Long scheduleId, @Valid ScheduleReplyRequest scheduleReplyRequest) {
        if(scheduleReplyRequest.getScheduleReplyState() == ScheduleReplyState.MEMBER_CANCEL && (scheduleReplyRequest.getMemo() == null ||scheduleReplyRequest.getMemo().isBlank())){
            throw new BusinessException(ErrorCode.SCHEDULE_CANCEL_MEMO_REQUIRED);
        }
        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Schedule scheduleMemberId = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        if(!member.getMemberId().equals(scheduleMemberId.getMemberId())){
            throw new BusinessException(ErrorCode.SCHEDULE_MEMBER_MISMATCH);
        }

        Schedule schedule = Schedule.builder()
                .memberId(member.getMemberId())
                .scheduleId(scheduleId)
                .scheduleReplyState(scheduleReplyRequest.getScheduleReplyState())
                .memo(scheduleReplyRequest.getMemo())
                .build();

        scheduleMapper.replyReservation(schedule);
    }
}
