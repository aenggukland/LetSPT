package com.aenggukland.letspt.board;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private MemberMapper memberMapper;

    // ===== 게시판 다건 조회 =====

    @Test
    @DisplayName("게시판 목록 조회 성공 - 검색 조건 없음")
    void getBoardList_success_noSearch() {
        // given
        BoardSearchRequest request = new BoardSearchRequest(BoardCategory.LESSON, 1, null, null);
        List<Board> boards = List.of(
                Board.builder().boardId(1L).category(BoardCategory.LESSON).title("제목1").build(),
                Board.builder().boardId(2L).category(BoardCategory.LESSON).title("제목2").build()
        );
        given(boardMapper.getBoardList(any(BoardSearchRequest.class))).willReturn(boards);

        // when
        List<Board> result = boardService.getBoardList(request);

        // then
        assertThat(result).hasSize(2);
        verify(boardMapper).getBoardList(any(BoardSearchRequest.class));
    }

    @Test
    @DisplayName("게시판 목록 조회 성공 - 제목 검색")
    void getBoardList_success_titleSearch() {
        // given
        BoardSearchRequest request = new BoardSearchRequest(BoardCategory.DIET, 1, BoardSearchCategory.TITLE, "운동");
        List<Board> boards = List.of(
                Board.builder().boardId(1L).category(BoardCategory.DIET).title("운동 식단").build()
        );
        given(boardMapper.getBoardList(any(BoardSearchRequest.class))).willReturn(boards);

        // when
        List<Board> result = boardService.getBoardList(request);

        // then
        assertThat(result).hasSize(1);
        verify(boardMapper).getBoardList(any(BoardSearchRequest.class));
    }

    @Test
    @DisplayName("게시판 목록 조회 실패 - 페이지 번호 0 이하")
    void getBoardList_fail_invalidPageNum() {
        // when & then
        assertThatThrownBy(() -> new BoardSearchRequest(BoardCategory.LESSON, 0, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_BOARD_PAGE_NUM.getMessage());
    }
}
