package com.aenggukland.letspt.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<List<Comment>> getCommentList(@PathVariable Long boardId){
        return ResponseEntity.ok().body(commentService.getCommentList(boardId));
    }
}
