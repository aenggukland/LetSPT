package com.aenggukland.letspt.fcm;

public enum FcmType {
    CHAT("새 메시지"),
    SCHEDULE_REQUEST("일정 요청"),
    SCHEDULE_CONFIRM("일정 확인"),
    SCHEDULE_CANCEL("일정 취소");

    private final String title;

    FcmType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
