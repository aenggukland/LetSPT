package com.aenggukland.letspt.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "게시글 생성 요청")
@Getter
public class BoardCreateRequest {

    @Schema(description = "카테고리 (LESSON / DIET / EXERCISE)", example = "DIET")
    @NotBlank
    @Pattern(regexp = "^(LESSON|DIET|EXERCISE)$", message = "LESSON, DIET, EXERCISE 중 하나여야 합니다.")
    private String category;

    @Schema(description = "게시글 제목 (최대 200자)", example = "오늘의 식단 공유")
    @NotBlank
    @Size(max = 200)
    private String title;

    @Schema(description = "게시글 내용", example = "닭가슴살 200g + 현미밥 150g")
    private String content;

    @Schema(description = "LESSON 카테고리 전용: 레슨 대상 회원 ID", example = "42")
    private Long memberId;
}
