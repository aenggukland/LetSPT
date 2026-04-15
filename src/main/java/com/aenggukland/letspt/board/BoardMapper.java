package com.aenggukland.letspt.board;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

// 게시글 데이터 접근 Mapper: SQL은 BoardMapper.xml에만 작성한다
@Mapper
public interface BoardMapper {

    // boardId로 단건 조회 (삭제된 게시글 제외)
    Optional<Board> findById(Long boardId);

    // 게시글 다건 조회 및 검색
    List<Board> getBoardList(BoardSearchRequest boardSearchRequest);

    // 마이페이지 LESSON 카드: 대상 회원(memberId) 기준으로 최근 N건을 조회한다
    List<BoardSummary> findRecentLessonsByMemberId(@Param("memberId") Long memberId,
                                                    @Param("limit") int limit);

    // 마이페이지 DIET/EXERCISE 카드: 작성자(authorId)와 카테고리 기준으로 최근 N건을 조회한다
    List<BoardSummary> findRecentByAuthor(@Param("authorId") Long authorId,
                                          @Param("category") String category,
                                          @Param("limit") int limit);

    // LESSON 전체 목록: 대상 회원(memberId) 기준으로 전체 조회
    List<Board> findAllLessonsByMemberId(Long memberId);

    // DIET/EXERCISE 전체 목록: 작성자(authorId)와 카테고리 기준으로 전체 조회
    List<Board> findAllByAuthor(@Param("authorId") Long authorId,
                                @Param("category") String category);

    // 새 게시글 저장, 생성된 boardId를 엔티티에 자동 주입
    void save(Board board);

    // 게시글 부분 수정: null이 아닌 필드만 업데이트 (<if> 활용)
    void update(@Param("request") BoardUpdateRequest request,
                @Param("boardId") Long boardId);

    // 소프트 삭제: is_deleted = TRUE로 설정
    void softDelete(Long boardId);
}
