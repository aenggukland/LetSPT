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

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final ChatMapper chatMapper;
    private final MemberMapper memberMapper;
    private final FcmTokenService fcmTokenService;

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

        Member member = memberMapper.findById(request.getSenderId()).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole memberRole = MemberRole.fromRoleId(member.getRoleId());
        Long receiverId = (memberRole == MemberRole.MEMBER ? chatMapper.getChatRoomTrainerId(request.getChatRoomId()) : chatMapper.getChatRoomMemberId(request.getChatRoomId()));
        fcmTokenService.sendPush(receiverId, FcmType.CHAT, request.getChatContent(), request.getChatRoomId());
    }
}
