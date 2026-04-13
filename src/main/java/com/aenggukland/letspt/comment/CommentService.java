package com.aenggukland.letspt.comment;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public void createComment(String username, Long boardId, @Valid CommentWriteRequest request) {
        //사용자 인증
        Member member = memberCertification(username);
        Comment comment = Comment.builder()
                .boardId(boardId)
                .authorId(member.getMemberId())
                .comment(request.getContent())
                .build();
        commentMapper.createComment(comment);
    }

    // 사용자 인증 함수
    private Member memberCertification(String username) {
        return memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
