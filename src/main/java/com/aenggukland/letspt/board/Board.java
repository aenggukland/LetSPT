package com.aenggukland.letspt.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 게시글 엔티티: board 테이블과 1:1 매핑된다
// category 컬럼은 BoardMapper.xml의 EnumTypeHandler로 Enum 변환이 처리된다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long boardId;
    private Long imageId;   // image 테이블 FK (현재 미사용, TODO F2)
    private Long authorId;  // 게시글 작성자 member_id
    private Long memberId;  // LESSON 카테고리의 대상 회원 member_id (DIET·EXERCISE는 null)
    private BoardCategory category;
    private String title;
    private String content;
    private Boolean isDeleted; // 소프트 삭제 플래그
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
