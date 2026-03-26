package com.aenggukland.letspt.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 게시글 수정 요청 DTO: 제목은 필수, 내용은 선택이다
@Getter
public class BoardUpdateRequest {

    // 게시글 제목: 최대 200자, 필수 입력
    @NotBlank
    @Size(max = 200)
    private String title;

    private String content;
}
