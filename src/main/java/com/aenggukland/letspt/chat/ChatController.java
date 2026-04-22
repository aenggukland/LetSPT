package com.aenggukland.letspt.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "1:1 채팅 API — 채팅방 생성/조회, 메시지 조회/삭제 (JWT 인증 필수)")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성 (트레이너)", description = "트레이너가 특정 회원과의 채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @PostMapping("/new/{memberId}")
    public ResponseEntity<Void> makeChatRoom(
            @RequestAttribute("username") String username,
            @Parameter(description = "채팅 상대 회원 ID") @PathVariable Long memberId){
        chatService.makeChatRoom(username, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 목록 조회", description = "본인이 참여 중인 모든 채팅방 목록(최신 메시지 포함)을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(@RequestAttribute("username") String username){
        return ResponseEntity.ok().body(chatService.getChatRoomList(username));
    }

    @Operation(summary = "채팅 내용 조회", description = "특정 채팅방의 메시지 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "채팅방 없음")
    })
    @GetMapping("/detail/{chatRoomId}")
    public ResponseEntity<List<ChatDetailResponse>> getChatDetailList(
            @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
            @RequestAttribute("username") String username){
        return ResponseEntity.ok().body(chatService.getChatDetailList(chatRoomId, username));
    }

    @Operation(summary = "채팅 메시지 삭제", description = "특정 채팅 메시지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "메시지 없음")
    })
    @DeleteMapping("/{chatRoomId}/message/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
            @Parameter(description = "메시지 ID") @PathVariable Long chatId,
            @RequestAttribute("username") String username){
        chatService.deleteChat(chatRoomId, chatId, username);
        return ResponseEntity.noContent().build();
    }

}
