package com.aenggukland.letspt.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "댓글 작성/수정 요청")
@Getter
@AllArgsConstructor
public class CommentRequest {

    @Schema(description = "댓글 내용", example = "좋은 식단이네요!")
    @NotBlank
    private String content;
}
