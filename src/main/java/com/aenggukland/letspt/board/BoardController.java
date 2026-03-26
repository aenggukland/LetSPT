package com.aenggukland.letspt.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getDetail(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getDetail(boardId));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestAttribute("username") String username,
                                       @RequestBody @Valid BoardCreateRequest request) {
        boardService.create(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Void> update(@RequestAttribute("username") String username,
                                       @PathVariable Long boardId,
                                       @RequestBody @Valid BoardUpdateRequest request) {
        boardService.update(username, boardId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@RequestAttribute("username") String username,
                                       @PathVariable Long boardId) {
        boardService.delete(username, boardId);
        return ResponseEntity.noContent().build();
    }
}
