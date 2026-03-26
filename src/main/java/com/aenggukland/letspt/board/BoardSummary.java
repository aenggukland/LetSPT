package com.aenggukland.letspt.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 마이페이지 카드에서 사용하는 게시글 요약 DTO
// 전체 Board 엔티티 대신 boardId·title·createdAt만 조회해 불필요한 데이터 전송을 줄인다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSummary {
    private Long boardId;
    private String title;
    private LocalDateTime createdAt;
}
