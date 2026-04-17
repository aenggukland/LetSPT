package com.aenggukland.letspt.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 게시글 댓글 REST API 컨트롤러
// URL에 boardId를 포함시켜 어느 게시글의 댓글인지 명시하는 구조이다
// 조회는 인증 불필요, 작성/수정/삭제는 JWT 인증이 필요하다
@RestController
@RequestMapping("/api/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회: 해당 게시글의 전체 댓글을 반환한다
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentList(@PathVariable Long boardId){
        return ResponseEntity.ok().body(commentService.getCommentList(boardId));
    }

    // 댓글 작성: 인증된 사용자가 특정 게시글에 댓글을 등록한다
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestAttribute("username") String username, @PathVariable Long boardId, @RequestBody @Valid CommentRequest request){
        commentService.createComment(username, boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 댓글 수정: 작성자 본인만 댓글 내용을 수정할 수 있다
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@RequestAttribute("username") String username, @PathVariable Long commentId, @RequestBody @Valid CommentRequest request){
        commentService.updateComment(username, commentId, request);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제: 작성자 본인만 삭제할 수 있다
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestAttribute("username") String username, @PathVariable Long commentId){
        commentService.deleteComment(username, commentId);
        return ResponseEntity.noContent().build();
    }
}
