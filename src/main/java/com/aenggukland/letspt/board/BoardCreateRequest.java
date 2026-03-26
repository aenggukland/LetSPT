package com.aenggukland.letspt.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardCreateRequest {

    @NotBlank
    @Pattern(regexp = "^(LESSON|DIET|EXERCISE)$", message = "LESSON, DIET, EXERCISE 중 하나여야 합니다.")
    private String category;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String content;

    private Long memberId; // LESSON 카테고리만 사용 (대상 회원 ID)
}
