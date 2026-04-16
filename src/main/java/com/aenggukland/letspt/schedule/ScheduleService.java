package com.aenggukland.letspt.schedule;

import com.aenggukland.letspt.common.CommonMethod;
import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.fcm.FcmTokenService;
import com.aenggukland.letspt.fcm.FcmType;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// 일정관리 비즈니스 로직을 처리하는 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final MemberMapper memberMapper;
    private final FcmTokenService fcmTokenService;

    // 회원,트레이너 수업 조회
    public ScheduleListResponse getSchedule(String username) {
        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole role = MemberRole.fromRoleId(member.getRoleId());
        List<ScheduleResponse> scheduleResponse = new ArrayList<>();
        // 학생
        if(role == MemberRole.MEMBER){
            scheduleResponse = scheduleMapper.getMemberSchedule(member.getMemberId());
        // 트레이너, 마스터
        }else {
            scheduleResponse = scheduleMapper.getTrainerSchedule(member.getMemberId());
        }
        return ScheduleListResponse.builder()
                .memberRole(role)
                .schedules(scheduleResponse)
                .build();
    }

    // 트레이너 -> 사용자 일정 확인 요청
    public void reservation(String username, ScheduleCreateRequest scheduleCreateRequest) {
        Member trainerInfo = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        // 조회한 트레이너의 권한이 트레이너여야함
        MemberRole role = MemberRole.fromRoleId(trainerInfo.getRoleId());
        if(role != MemberRole.TRAINER && role != MemberRole.MASTER){
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        // 회원 존재 확인
        if(memberMapper.findById(scheduleCreateRequest.getMemberId()).isEmpty()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Schedule schedule = Schedule.builder()
                .startDateTime(scheduleCreateRequest.getStartDateTime())
                .endDateTime(scheduleCreateRequest.getEndDateTime())
                .build();

        // 같은 시간대 수업 중복체크
        int trainerPtCnt = scheduleMapper.getTrainerPtCnt(trainerInfo.getMemberId(), schedule);
        if(trainerPtCnt > 0){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_PT_DUPLICATION);
        }
        scheduleMapper.reservation(trainerInfo.getMemberId(), scheduleCreateRequest);

        String startTime = CommonMethod.formatDateTime(scheduleCreateRequest.getStartDateTime());
        String endTime = CommonMethod.formatDateTime(scheduleCreateRequest.getEndDateTime());
        String fcmBody = trainerInfo.getName() + "님이 " + startTime + " 부터 " + endTime + " 까지 " + scheduleCreateRequest.getClassContent() + " 수업 요청을 보냈습니다.";
        fcmTokenService.sendPush(scheduleCreateRequest.getMemberId(), FcmType.SCHEDULE_REQUEST, fcmBody, scheduleCreateRequest.getScheduleId());
    }

    // 회원 -> 트레이너 일정 요청 수락/거절
    public void replyReservation(String username, Long scheduleId, ScheduleReplyRequest scheduleReplyRequest) {
        // 거절일때 거절 사유 요청값 필수
        if(scheduleReplyRequest.getScheduleReplyState() == ScheduleReplyState.MEMBER_CANCEL && (scheduleReplyRequest.getMemo() == null ||scheduleReplyRequest.getMemo().isBlank())){
            throw new BusinessException(ErrorCode.SCHEDULE_CANCEL_MEMO_REQUIRED);
        }

        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Schedule checkSchedule = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        // 수업 회원ID와 요청 회원ID가 일치해야함
        if(!member.getMemberId().equals(checkSchedule.getMemberId())){
            throw new BusinessException(ErrorCode.SCHEDULE_MEMBER_MISMATCH);
        }
        // 예약 요청 상태의 수업만 수락/거절 가능
        if(!checkSchedule.getState().equals(ScheduleState.RESERVATION)){
            throw new BusinessException(ErrorCode.SCHEDULE_REPLY_DENIED);
        }

        ScheduleState state = switch(scheduleReplyRequest.getScheduleReplyState()) {
            case COMPLETE -> ScheduleState.COMPLETE;
            case MEMBER_CANCEL -> ScheduleState.MEMBER_CANCEL;
        };

        Schedule schedule = Schedule.builder()
                .memberId(member.getMemberId())
                .scheduleId(scheduleId)
                .state(state)
                .memo(scheduleReplyRequest.getMemo())
                .build();

        scheduleMapper.replyReservation(schedule);

        String startTime = CommonMethod.formatDateTime(checkSchedule.getStartDateTime());
        String endTime = CommonMethod.formatDateTime(checkSchedule.getEndDateTime());
        String stateStr = state == ScheduleState.COMPLETE ? "수락" : "거절";
        String fcmBody = member.getName() + "님이 " + startTime + " 부터 " + endTime + " 까지 " + checkSchedule.getClassContent() + " 수업 요청을 " + stateStr + " 했습니다.";
        FcmType alarmType = state == ScheduleState.MEMBER_CANCEL ? FcmType.SCHEDULE_CANCEL : FcmType.SCHEDULE_CONFIRM;
        fcmTokenService.sendPush(checkSchedule.getTrainerId(), alarmType, fcmBody, scheduleId);
    }

    // 트레이너가 예약 내용 수정(예약 요청 상태인 수업만 가능)
    public void updateReservation(String username, Long scheduleId, ScheduleUpdateRequest scheduleUpdateRequest) {
        Member trainer = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        if(!trainer.getMemberId().equals(schedule.getTrainerId())){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_MISMATCH);
        }

        if(schedule.getState() != ScheduleState.RESERVATION){
            throw new BusinessException(ErrorCode.SCHEDULE_UPDATE_DENIED);
        }

        Schedule updateSchedule = Schedule.builder()
                .scheduleId(scheduleId)
                .startDateTime(scheduleUpdateRequest.getStartDateTime())
                .endDateTime(scheduleUpdateRequest.getEndDateTime())
                .classContent(scheduleUpdateRequest.getClassContent())
                .build();

        int trainerPtCnt = scheduleMapper.getTrainerPtCnt(trainer.getMemberId(), updateSchedule);
        if(trainerPtCnt > 0){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_PT_DUPLICATION);
        }

        int updatedCount = scheduleMapper.updateReservation(updateSchedule);
        if(updatedCount == 0){
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_MODIFIABLE);
        }

        String startTime = CommonMethod.formatDateTime(scheduleUpdateRequest.getStartDateTime());
        String endTime = CommonMethod.formatDateTime(scheduleUpdateRequest.getEndDateTime());
        String fcmBody = trainer.getName() + "님이 " + startTime + " 부터 " + endTime + " 까지 " + scheduleUpdateRequest.getClassContent() + " 수업 요청을 보냈습니다.";
        fcmTokenService.sendPush(schedule.getMemberId(), FcmType.SCHEDULE_REQUEST, fcmBody, scheduleId);
    }

    // 트레이너가 수업 취소
    public void cancelReservation(String username, Long scheduleId, @Valid ScheduleCancelRequest scheduleCancelRequest) {
        Member trainer = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Schedule checkSchedule = scheduleMapper.findByScheduleId(scheduleId).orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
        if(!trainer.getMemberId().equals(checkSchedule.getTrainerId())){
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_MISMATCH);
        }
        // 일정 요청, 수락, 회원 취소 상태인 수업만 취소 가능
        ScheduleState scheduleState = checkSchedule.getState();
        if(!List.of(ScheduleState.RESERVATION, ScheduleState.COMPLETE, ScheduleState.MEMBER_CANCEL)
                .contains(scheduleState)){
            throw new BusinessException(ErrorCode.SCHEDULE_CANCEL_STATE_MISMATCH);
        }
        ScheduleState cancelState = ScheduleState.CANCEL;
        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .memo(scheduleCancelRequest.getMemo())
                .state(cancelState)
                .build();

        int cancelSchedule = scheduleMapper.cancelReservation(schedule);
        if(cancelSchedule == 0){
            throw new BusinessException(ErrorCode.SCHEDULE_CANCEL_FAILED);
        }

        String startTime = CommonMethod.formatDateTime(checkSchedule.getStartDateTime());
        String endTime = CommonMethod.formatDateTime(checkSchedule.getEndDateTime());
        String fcmBody = trainer.getName() + "님이 " + startTime + " 부터 " + endTime + " 까지의 수업을 취소했습니다.";
        fcmTokenService.sendPush(checkSchedule.getMemberId(), FcmType.SCHEDULE_CANCEL, fcmBody, scheduleId);
    }
}
