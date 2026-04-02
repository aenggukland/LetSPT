package com.aenggukland.letspt.schedule;

// RESERVATION(예약생성), COMPLETE(예약확정), CANCEL(트레이너-예약취소), MEMBER_CANCEL(회원-예약취소)
public enum ScheduleStatus {
    RESERVATION,
    COMPLETE,
    CANCEL,
    MEMBER_CANCEL;
}
