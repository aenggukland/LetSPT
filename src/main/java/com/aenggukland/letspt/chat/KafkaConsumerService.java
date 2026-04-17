package com.aenggukland.letspt.chat;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.fcm.FcmTokenService;
import com.aenggukland.letspt.fcm.FcmType;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

// Kafka 메시지 소비 서비스: "chat" 토픽을 구독해 채팅 메시지를 처리한다
// DB 저장 → WebSocket 브로드캐스트 → FCM 푸시 알림 순서로 처리한다
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final ChatMapper chatMapper;
    private final MemberMapper memberMapper;
    private final FcmTokenService fcmTokenService;

    // "chat" 토픽 메시지 소비:
    // 1. JSON을 ChatMessageRequest로 역직렬화
    // 2. DB에 채팅 메시지 저장
    // 3. 해당 채팅방에 열린 WebSocket 세션들에게 메시지 브로드캐스트
    // 4. 수신자(발신자의 상대방)에게 FCM 푸시 알림 발송
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

        // 발신자 역할에 따라 수신자를 결정: 회원이 보내면 트레이너에게, 트레이너가 보내면 회원에게 FCM 전송
        Member member = memberMapper.findById(request.getSenderId()).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole memberRole = MemberRole.fromRoleId(member.getRoleId());
        Long receiverId = (memberRole == MemberRole.MEMBER ? chatMapper.getChatRoomTrainerId(request.getChatRoomId()) : chatMapper.getChatRoomMemberId(request.getChatRoomId()));
        fcmTokenService.sendPush(receiverId, FcmType.CHAT, request.getChatContent(), request.getChatRoomId());
    }
}
