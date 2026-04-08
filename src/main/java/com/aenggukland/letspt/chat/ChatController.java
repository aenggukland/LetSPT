package com.aenggukland.letspt.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
