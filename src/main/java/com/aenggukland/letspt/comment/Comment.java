package com.aenggukland.letspt.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Comment {
    private Long commentId;
    private Long boardId;
    private Long authorId;
    private String authorName;
    private String comment;
    private LocalDateTime createdAt;
}
