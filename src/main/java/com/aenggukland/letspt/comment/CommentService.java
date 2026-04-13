package com.aenggukland.letspt.comment;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper;

    // 댓글 목록 조회
    public List<Comment> getCommentList(Long boardId){
        return commentMapper.getCommentList(boardId);
    }

    // 댓글 작성
    public void createComment(String username, Long boardId, CommentRequest request) {
        //사용자 인증 및 정보 조회
        Member member = memberCertification(username);
        Comment comment = Comment.builder()
                .boardId(boardId)
                .authorId(member.getMemberId())
                .comment(request.getContent())
                .build();
        commentMapper.createComment(comment);
    }

    // 댓글 수정
    public void updateComment(String username, Long commentId, CommentRequest request) {
        //사용자 인증 및 정보 조회
        Member member = memberCertification(username);
        commentCertification(member.getMemberId(), commentId);
        Comment comment = Comment.builder()
                .commentId(commentId)
                .comment(request.getContent())
                .build();
        int updateCnt = commentMapper.updateComment(comment);
        if(updateCnt < 1){
            throw new BusinessException(ErrorCode.COMMENT_UPDATE_FAILED);
        }
    }

    // 사용자 인증 함수
    private Member memberCertification(String username) {
        return memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 사용자 작성 댓글 인증
    private void commentCertification(Long memberId, Long commentId) {
        Long authorId = commentMapper.getCommentAuthorId(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_AUTHOR_NOT_FOUND));
        if(!Objects.equals(memberId, authorId)){
            throw new BusinessException(ErrorCode.COMMENT_AUTHOR_CERTIFICATION_FAILED);
        }
    }
}
