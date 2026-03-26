package com.aenggukland.letspt.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    private String content;
}
