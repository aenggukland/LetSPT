package com.aenggukland.letspt.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentWriteRequest {
    @NotBlank
    private String content;
}
