package com.aenggukland.letspt.comment;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> getCommentList(Long boardId);
}
