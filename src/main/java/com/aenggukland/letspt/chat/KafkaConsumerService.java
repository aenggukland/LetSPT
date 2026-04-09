package com.aenggukland.letspt.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final ChatMapper chatMapper;

    @KafkaListener(topics = "chat", groupId = "chat-group")
    public void consume(String message) throws Exception {
        ChatMessageRequest request = objectMapper.readValue(message, ChatMessageRequest.class);

        // DB에 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(request.getChatRoomId())
                .senderId(request.getSenderId())
                .messageContent(request.getChatContent())
                .isRead(false)
                .isDeleted(false)
                .build();
        chatMapper.insertMessage(chatMessage);

        // 해당 채팅방 세션들에게 전달
        List<WebSocketSession> sessions = chatWebSocketHandler.getChatRooms().get(request.getChatRoomId());
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }
}
