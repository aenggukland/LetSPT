package com.aenggukland.letspt.board;

import lombok.Getter;

@Getter
public class BoardCreateRequest {
    private String category;  // "LESSON", "DIET", "EXERCISE"
    private String title;
    private String content;
    private Long memberId;    // LESSON 카테고리만 사용 (대상 회원 ID)
}
