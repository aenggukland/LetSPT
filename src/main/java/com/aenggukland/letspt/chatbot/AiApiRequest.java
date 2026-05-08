package com.aenggukland.letspt.chatbot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 외부 AI API에 전송하는 요청 바디 (OpenAI Chat Completions 호환 형식)
@Getter
@AllArgsConstructor
public class AiApiRequest {
    private String model;
    private List<AiChatMessage> messages;
}
