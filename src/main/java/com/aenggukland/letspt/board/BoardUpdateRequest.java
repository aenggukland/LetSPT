package com.aenggukland.letspt.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "게시글 수정 요청")
@Getter
public class BoardUpdateRequest {

    @Schema(description = "게시글 제목 (최대 200자)", example = "수정된 식단 공유")
    @NotBlank
    @Size(max = 200)
    private String title;

    @Schema(description = "게시글 내용", example = "닭가슴살 250g + 고구마 100g")
    private String content;
}
