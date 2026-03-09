package com.aenggukland.letspt.board;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {

    Optional<Board> findById(Long boardId);

    List<BoardSummary> findRecentLessonsByMemberId(@Param("memberId") Long memberId,
                                                    @Param("limit") int limit);

    List<BoardSummary> findRecentByAuthor(@Param("authorId") Long authorId,
                                          @Param("category") String category,
                                          @Param("limit") int limit);

    List<Board> findAllLessonsByMemberId(Long memberId);

    List<Board> findAllByAuthor(@Param("authorId") Long authorId,
                                @Param("category") String category);

    void save(Board board);

    void update(@Param("request") BoardUpdateRequest request,
                @Param("boardId") Long boardId);

    void softDelete(Long boardId);
}
