package com.aenggukland.letspt.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 트레이너와 회원간의 1:1 채팅
// 모든 엔드포인트는 JWT 인증이 필요하며, @RequestAttribute("username")으로 인증 사용자를 수신한다
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 트레이너의 채팅방생성
    @PostMapping("/new/{memberId}")
    public ResponseEntity<Void> makeChatRoom(@RequestAttribute("username") String username, @PathVariable Long memberId){
        chatService.makeChatRoom(username, memberId);
        return ResponseEntity.ok().build();
    }

    //채팅방 조회
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(@RequestAttribute("username") String username){
        return ResponseEntity.ok().body(chatService.getChatRoomList(username));
    }

    //채팅내용 조회
    @GetMapping("/detail/{chatRoomId}")
    public ResponseEntity<List<ChatDetailResponse>> getChatDetailList(@PathVariable Long chatRoomId, @RequestAttribute("username") String username){
        return ResponseEntity.ok().body(chatService.getChatDetailList(chatRoomId, username));
    }

}
