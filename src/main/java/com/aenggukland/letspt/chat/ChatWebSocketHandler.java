package com.aenggukland.letspt.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, List<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long roomId = Long.parseLong(session.getUri().getPath().split("/")[3]);
        chatRooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        kafkaProducerService.sendMessage(message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        chatRooms.values().forEach(sessions -> sessions.remove(session));
    }

    public Map<Long, List<WebSocketSession>> getChatRooms() {
        return chatRooms;
    }
}