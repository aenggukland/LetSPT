package com.aenggukland.letspt.chatbot;

import lombok.AllArgsConstructor;
import lombok.Getter;

// OpenAI 호환 API의 단일 메시지 객체 (role: system/user/assistant)
@Getter
@AllArgsConstructor
public class AiChatMessage {
    private String role;
    private String content;
}
