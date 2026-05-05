package com.aenggukland.letspt.schedule;

import com.aenggukland.letspt.common.CommonMethod;
import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.fcm.FcmTokenService;
import com.aenggukland.letspt.fcm.FcmType;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import com.aenggukland.letspt.ptticket.PtTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// 수업 일정 비즈니스 로직을 처리하는 서비스
// 트레이너의 일정 요청/수정/취소, 회원의 수락/거절을 처리하며
// 각 상태 변경 시 FCM 푸시 알림을 발송한다
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final MemberMapper memberMapper;
    private final FcmTokenService fcmTokenService;
    private final PtTicketService ptTicketService;

    // 수업 일정 조회: 역할에 따라 회원용(memberId 기준)/트레이너용(trainerId 기준) 일정을 반환한다
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

    // 수업 요청(트레이너 → 회원): 권한·회원 존재·시간 중복을 검증하고 RESERVATION 상태로 저장한다
    // 저장 후 대상 회원에게 FCM 푸시를 발송한다
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

    // 수업 요청 수락/거절(회원 → 트레이너): RESERVATION 상태인 일정에 대해서만 처리한다
    // 거절(MEMBER_CANCEL) 시 메모(사유)가 필수이며, 처리 후 트레이너에게 FCM을 발송한다
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

    // 수업 수정(트레이너): RESERVATION 상태인 일정만 수정 가능하며 시간 중복을 재검증한다
    // 수정 후 회원에게 FCM 푸시를 재발송한다
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

    // 수업 완료 처리(트레이너): COMPLETE 상태인 일정을 FINISH로 변경하고 횟수권을 1회 차감한다
    public void finishSchedule(String username, Long scheduleId) {
        Member trainer = memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleMapper.findByScheduleId(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!trainer.getMemberId().equals(schedule.getTrainerId())) {
            throw new BusinessException(ErrorCode.SCHEDULE_TRAINER_MISMATCH);
        }
        if (schedule.getState() != ScheduleState.COMPLETE) {
            throw new BusinessException(ErrorCode.SCHEDULE_FINISH_DENIED);
        }

        scheduleMapper.finishSchedule(scheduleId);
        ptTicketService.deductActiveTicket(schedule.getMemberId(), trainer.getMemberId());

        String startTime = CommonMethod.formatDateTime(schedule.getStartDateTime());
        String endTime = CommonMethod.formatDateTime(schedule.getEndDateTime());
        String fcmBody = trainer.getName() + "님이 " + startTime + " 부터 " + endTime + " 까지 " + schedule.getClassContent() + " 수업을 완료 처리했습니다.";
        fcmTokenService.sendPush(schedule.getMemberId(), FcmType.SCHEDULE_FINISH, fcmBody, scheduleId);
    }

    // 수업 취소(트레이너): RESERVATION·COMPLETE·MEMBER_CANCEL 상태의 일정을 CANCEL로 변경한다
    // 취소 후 회원에게 FCM 알림을 발송한다
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
