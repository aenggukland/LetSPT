package com.aenggukland.letspt.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {
    private Long senderId;
    private Long chatRoomId;
    private String chatContent;
}
