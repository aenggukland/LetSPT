package com.aenggukland.letspt.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSummary {
    private Long boardId;
    private String title;
    private LocalDateTime createdAt;
}
