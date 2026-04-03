package com.aenggukland.letspt.schedule;

// 회원의 예약 취소 시 예약 확정(COMPLETE), 예약 취소(MEMBER_CANCEL) 두개의 ENUM 사용
public enum ScheduleReplyState {
    COMPLETE,     // 회원이 수락
    MEMBER_CANCEL // 회원 취소
}