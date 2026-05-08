package com.aenggukland.letspt.dietfeedback;

public enum DietFeedbackType {
    THUMBS_UP("따봉"),
    X("거절"),
    CHECK("체크");

    private final String label;

    DietFeedbackType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
