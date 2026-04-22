package com.aenggukland.letspt.board;

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

@Tag(name = "Board", description = "게시판 API — 레슨/식단/운동 게시글 CRUD. 조회는 인증 불필요, 나머지는 JWT 인증 필수")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 단건 조회", description = "인증 없이 조회 가능합니다. 삭제된 게시글은 404를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음 또는 삭제됨")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getDetail(@Parameter(description = "게시글 ID") @PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getDetail(boardId));
    }

    @Operation(summary = "게시글 목록 조회 / 검색", description = "카테고리·키워드·페이지로 게시글 목록을 조회합니다. pageNum은 1 이상이어야 합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<Board>> getBoardList(@ModelAttribute BoardSearchRequest boardSearchRequest) {
        return ResponseEntity.ok(boardService.getBoardList(boardSearchRequest));
    }

    @Operation(summary = "게시글 생성", description = "카테고리별 작성 권한 검증 후 저장합니다. LESSON은 TRAINER·MASTER만 작성 가능하며 memberId 필수입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "403", description = "카테고리 작성 권한 없음"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 오류")
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestAttribute("username") String username,
                                       @RequestBody @Valid BoardCreateRequest request) {
        boardService.create(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "게시글 수정", description = "작성자 본인만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<Void> update(@RequestAttribute("username") String username,
                                       @Parameter(description = "게시글 ID") @PathVariable Long boardId,
                                       @RequestBody @Valid BoardUpdateRequest request) {
        boardService.update(username, boardId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제", description = "작성자 본인만 소프트 삭제(is_deleted = TRUE) 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@RequestAttribute("username") String username,
                                       @Parameter(description = "게시글 ID") @PathVariable Long boardId) {
        boardService.delete(username, boardId);
        return ResponseEntity.noContent().build();
    }
}
