package com.aenggukland.letspt.schedule;

// RESERVATION(예약생성), COMPLETE(예약확정), CANCEL(트레이너-예약취소), MEMBER_CANCEL(회원-예약취소)
public enum ScheduleState {
    RESERVATION,  // 트레이너가 일정 요청
    COMPLETE,     // 회원이 수락
    FINISH,       // PT 수업 완료
    CANCEL,       // 트레이너 취소
    MEMBER_CANCEL // 회원 취소
}