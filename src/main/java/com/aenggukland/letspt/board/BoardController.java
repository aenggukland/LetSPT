package com.aenggukland.letspt.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 게시글 관련 REST API 엔드포인트를 처리하는 컨트롤러
// 게시글 단건 조회, 생성, 수정, 삭제 요청을 BoardService에 위임한다
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 단건 조회: 인증 불필요, 삭제된 게시글은 404를 반환한다
    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getDetail(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getDetail(boardId));
    }

    // 게시글 생성: 카테고리별 작성 권한을 검증한 후 저장한다
    // LESSON은 TRAINER·MASTER만, DIET·EXERCISE·FEEDBACK·BOAST는 모든 역할이 작성 가능하다
    @PostMapping
    public ResponseEntity<Void> create(@RequestAttribute("username") String username,
                                       @RequestBody @Valid BoardCreateRequest request) {
        boardService.create(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 게시글 수정: 작성자 본인만 수정 가능하다
    @PutMapping("/{boardId}")
    public ResponseEntity<Void> update(@RequestAttribute("username") String username,
                                       @PathVariable Long boardId,
                                       @RequestBody @Valid BoardUpdateRequest request) {
        boardService.update(username, boardId, request);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제: 작성자 본인만 소프트 삭제(is_deleted = TRUE) 처리한다
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@RequestAttribute("username") String username,
                                       @PathVariable Long boardId) {
        boardService.delete(username, boardId);
        return ResponseEntity.noContent().build();
    }
}
