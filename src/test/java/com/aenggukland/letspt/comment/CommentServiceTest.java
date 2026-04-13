package com.aenggukland.letspt.comment;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private MemberMapper memberMapper;

    // ===== 댓글 목록 조회 =====

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getCommentList_success() {
        // given
        Long boardId = 1L;
        List<Comment> comments = List.of(
                Comment.builder().commentId(1L).boardId(boardId).authorId(1L).comment("댓글1").build(),
                Comment.builder().commentId(2L).boardId(boardId).authorId(2L).comment("댓글2").build()
        );
        given(commentMapper.getCommentList(boardId)).willReturn(comments);

        // when
        List<Comment> result = commentService.getCommentList(boardId);

        // then
        assertThat(result).hasSize(2);
        verify(commentMapper).getCommentList(boardId);
    }

    // ===== 댓글 작성 =====

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_success() {
        // given
        String username = "testuser";
        Long boardId = 1L;
        CommentRequest request = new CommentRequest("댓글 내용");

        Member member = Member.builder().memberId(1L).username(username).build();
        given(memberMapper.findByUsername(username)).willReturn(Optional.of(member));

        // when
        commentService.createComment(username, boardId, request);

        // then
        verify(commentMapper).createComment(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 회원")
    void createComment_memberNotFound() {
        // given
        String username = "unknown";
        Long boardId = 1L;
        CommentRequest request = new CommentRequest("댓글 내용");

        given(memberMapper.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(username, boardId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    // ===== 댓글 수정 =====

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() {
        // given
        String username = "testuser";
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("수정된 댓글");

        Member member = Member.builder().memberId(1L).username(username).build();
        given(memberMapper.findByUsername(username)).willReturn(Optional.of(member));
        given(commentMapper.getCommentAuthorId(commentId)).willReturn(Optional.of(1L));
        given(commentMapper.updateComment(any(Comment.class))).willReturn(1);

        // when
        commentService.updateComment(username, commentId, request);

        // then
        verify(commentMapper).updateComment(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 불일치")
    void updateComment_authorMismatch() {
        // given
        String username = "testuser";
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("수정된 댓글");

        Member member = Member.builder().memberId(1L).username(username).build();
        given(memberMapper.findByUsername(username)).willReturn(Optional.of(member));
        given(commentMapper.getCommentAuthorId(commentId)).willReturn(Optional.of(99L)); // 다른 작성자

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(username, commentId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COMMENT_AUTHOR_CERTIFICATION_FAILED.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 실패 - DB 업데이트 실패")
    void updateComment_dbFailed() {
        // given
        String username = "testuser";
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("수정된 댓글");

        Member member = Member.builder().memberId(1L).username(username).build();
        given(memberMapper.findByUsername(username)).willReturn(Optional.of(member));
        given(commentMapper.getCommentAuthorId(commentId)).willReturn(Optional.of(1L));
        given(commentMapper.updateComment(any(Comment.class))).willReturn(0); // 업데이트 실패

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(username, commentId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COMMENT_UPDATE_FAILED.getMessage());
    }
}
