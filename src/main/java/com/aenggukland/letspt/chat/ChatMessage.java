package com.aenggukland.letspt.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String messageContent;
    private Boolean isRead;
    private Boolean isDeleted;
    private LocalDateTime sentAt;
}
