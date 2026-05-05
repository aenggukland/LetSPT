package com.aenggukland.letspt.fcm;

public enum FcmType {
    CHAT("새 메시지"),
    SCHEDULE_REQUEST("일정 요청"),
    SCHEDULE_CONFIRM("일정 확인"),
    SCHEDULE_CANCEL("트레이너의 일정 취소"),
    SCHEDULE_MEMBER_CANCEL("회원의 일정 취소"),
    SCHEDULE_TODAY_REMINDER("오늘 수업 알림"),
    SCHEDULE_FINISH("수업 완료");

    private final String title;

    FcmType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
