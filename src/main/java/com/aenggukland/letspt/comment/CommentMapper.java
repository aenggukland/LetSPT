package com.aenggukland.letspt.comment;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {
    List<Comment> getCommentList(Long boardId);

    void createComment(Comment comment);

    Optional<Long> getCommentAuthorId(java.lang.Long commentId);

    int updateComment(Comment comment);
}
