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

// WebSocket 기반 실시간 채팅 핸들러
// 클라이언트가 /ws/chat/{chatRoomId}로 연결하면 채팅방 세션을 관리한다
// 수신된 메시지는 Kafka로 전달하고, KafkaConsumerService가 소비해 세션들에게 브로드캐스트한다
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // chatRoomId → 연결된 WebSocket 세션 목록 (ConcurrentHashMap + CopyOnWriteArrayList로 스레드 안전 보장)
    private final Map<Long, List<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final KafkaProducerService kafkaProducerService;

    // 클라이언트 연결 시: URL에서 chatRoomId를 파싱해 해당 채팅방 세션 목록에 추가한다
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long roomId = Long.parseLong(session.getUri().getPath().split("/")[3]);
        chatRooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    // 메시지 수신 시: Kafka "chat" 토픽으로 메시지를 발행한다
    // 실제 DB 저장과 세션 브로드캐스트는 KafkaConsumerService에서 처리한다
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        kafkaProducerService.sendMessage(message.getPayload());
    }

    // 클라이언트 연결 해제 시: 모든 채팅방 세션 목록에서 해당 세션을 제거한다
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        chatRooms.values().forEach(sessions -> sessions.remove(session));
    }

    // KafkaConsumerService에서 채팅방 세션 목록에 접근할 때 사용하는 getter
    public Map<Long, List<WebSocketSession>> getChatRooms() {
        return chatRooms;
    }
}