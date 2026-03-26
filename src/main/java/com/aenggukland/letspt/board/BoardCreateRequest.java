package com.aenggukland.letspt.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 게시글 생성 요청 DTO
// category에 따라 작성 권한과 memberId 필수 여부가 달라진다
@Getter
public class BoardCreateRequest {

    // 카테고리: LESSON·DIET·EXERCISE 세 가지 값만 허용
    @NotBlank
    @Pattern(regexp = "^(LESSON|DIET|EXERCISE)$", message = "LESSON, DIET, EXERCISE 중 하나여야 합니다.")
    private String category;

    // 게시글 제목: 최대 200자
    @NotBlank
    @Size(max = 200)
    private String title;

    private String content;

    // LESSON 카테고리 전용: 레슨 대상 회원의 memberId (LESSON 작성 시 필수)
    private Long memberId;
}
