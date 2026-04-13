package com.aenggukland.letspt.board;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;

// 게시글 카테고리 Enum
// LESSON: 트레이너(TRAINER·MASTER)가 특정 회원에게 작성 / DIET·EXERCISE: 모든 역할이 작성 가능
public enum BoardCategory {
    LESSON, //수업 내용
    DIET, // 식단 공유
    EXERCISE, //개인 운동
    FEEDBACK, //피드백
    BOAST; // 자랑

    // 문자열로 카테고리를 안전하게 변환한다
    // valueOf()와 달리 매핑 실패 시 500 대신 400(INVALID_BOARD_CATEGORY)을 반환한다
    public static BoardCategory from(String value) {
        try {
            return BoardCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_BOARD_CATEGORY);
        }
    }
}
