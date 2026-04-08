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
public class ChatRoomListResponse {
    private String partnerName;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private String messageContent;
}
