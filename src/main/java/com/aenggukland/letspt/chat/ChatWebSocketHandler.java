package com.aenggukland.letspt.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    // 클라이언트 연결 시: 채팅방 참여자인지 검증 후 세션을 추가한다
    // JwtHandshakeInterceptor가 저장한 username으로 참여자를 확인하고, memberId를 세션에 저장한다
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = Long.parseLong(session.getUri().getPath().split("/")[3]);
        String username = (String) session.getAttributes().get("username");

        Long memberId = chatService.resolveParticipantId(username, roomId);
        if (memberId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        session.getAttributes().put("memberId", memberId);
        chatRooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    // 메시지 수신 시: 클라이언트가 보낸 senderId를 세션의 인증된 memberId로 덮어쓴 뒤 Kafka로 전달한다
    // 클라이언트가 senderId를 조작해 타인 명의로 메시지를 저장하는 것을 방지한다
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = (Long) session.getAttributes().get("memberId");

        Map<String, Object> payload = objectMapper.readValue(
                message.getPayload(), new TypeReference<Map<String, Object>>() {});
        payload.put("senderId", senderId);

        kafkaProducerService.sendMessage(objectMapper.writeValueAsString(payload));
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
