package com.aenggukland.letspt.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "댓글 API — 게시글별 댓글 조회/작성/수정/삭제. 조회는 인증 불필요, 나머지는 JWT 인증 필수")
@RestController
@RequestMapping("/api/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회", description = "해당 게시글의 전체 댓글을 반환합니다. 인증 불필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentList(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId){
        return ResponseEntity.ok().body(commentService.getCommentList(boardId));
    }

    @Operation(summary = "댓글 작성", description = "인증된 사용자가 특정 게시글에 댓글을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping
    public ResponseEntity<Void> createComment(
            @RequestAttribute("username") String username,
            @Parameter(description = "게시글 ID") @PathVariable Long boardId,
            @RequestBody @Valid CommentRequest request){
        commentService.createComment(username, boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "댓글 수정", description = "작성자 본인만 댓글 내용을 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @RequestAttribute("username") String username,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request){
        commentService.updateComment(username, commentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", description = "작성자 본인만 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @RequestAttribute("username") String username,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId){
        commentService.deleteComment(username, commentId);
        return ResponseEntity.noContent().build();
    }
}
