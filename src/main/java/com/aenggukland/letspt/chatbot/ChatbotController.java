package com.aenggukland.letspt.chatbot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chatbot", description = "AI 챗봇 API — PT·식단·운동 관련 질문에 AI가 답변 (JWT 인증 필수)")
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Operation(summary = "AI 챗봇 메시지 전송", description = "PT·운동·식단 관련 질문을 AI에게 전송하고 답변을 받습니다. 단일 턴 방식으로 대화 히스토리는 유지되지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 응답 성공"),
            @ApiResponse(responseCode = "400", description = "메시지 유효성 오류 (빈 값 또는 1000자 초과)"),
            @ApiResponse(responseCode = "502", description = "외부 AI API 호출 실패")
    })
    @PostMapping("/chat")
    public ResponseEntity<ChatbotResponse> chat(@RequestBody @Valid ChatbotRequest request) {
        return ResponseEntity.ok(chatbotService.chat(request.getMessage()));
    }
}
