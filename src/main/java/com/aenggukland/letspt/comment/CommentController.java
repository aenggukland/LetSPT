package com.aenggukland.letspt.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 조회
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentList(@PathVariable Long boardId){
        return ResponseEntity.ok().body(commentService.getCommentList(boardId));
    }

    //댓글 작성
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestAttribute("username") String username, @PathVariable Long boardId, @RequestBody @Valid CommentRequest request){
        commentService.createComment(username, boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@RequestAttribute("username") String username, @PathVariable Long commentId, @RequestBody @Valid CommentRequest request){
        commentService.updateComment(username, commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestAttribute("username") String username, @PathVariable Long commentId){
        commentService.deleteComment(username, commentId);
        return ResponseEntity.noContent().build();
    }
}
