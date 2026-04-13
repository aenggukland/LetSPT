package com.aenggukland.letspt.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    public List<Comment> getCommentList(Long boardId){
        return commentMapper.getCommentList(boardId);
    }
}
