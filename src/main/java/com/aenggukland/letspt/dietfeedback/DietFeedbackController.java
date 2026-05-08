package com.aenggukland.letspt.dietfeedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DietFeedback", description = "식단 피드백 API — 트레이너가 회원의 식단 게시글에 이모지 피드백(따봉·X·체크) 부여")
@RestController
@RequestMapping("/api/diet-feedback")
@RequiredArgsConstructor
public class DietFeedbackController {

    private final DietFeedbackService dietFeedbackService;

    @Operation(summary = "식단 피드백 등록/수정", description = "TRAINER·MASTER만 가능. 이미 피드백이 있으면 타입을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 또는 수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 또는 DIET 카테고리가 아닌 게시글"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/{boardId}")
    public ResponseEntity<Void> giveFeedback(
            @RequestAttribute("username") String username,
            @Parameter(description = "식단 게시글 ID") @PathVariable Long boardId,
            @RequestBody @Valid DietFeedbackRequest request) {
        dietFeedbackService.giveFeedback(username, boardId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "식단 피드백 삭제", description = "피드백을 등록한 트레이너 본인만 삭제 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "피드백 없음")
    })
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @RequestAttribute("username") String username,
            @Parameter(description = "피드백 ID") @PathVariable Long feedbackId) {
        dietFeedbackService.deleteFeedback(username, feedbackId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글별 피드백 목록 조회", description = "특정 식단 게시글에 달린 모든 트레이너 피드백을 조회합니다. 인증 불필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<DietFeedbackResponse>> getFeedbackByBoard(
            @Parameter(description = "식단 게시글 ID") @PathVariable Long boardId) {
        return ResponseEntity.ok(dietFeedbackService.getFeedbackByBoard(boardId));
    }
}
