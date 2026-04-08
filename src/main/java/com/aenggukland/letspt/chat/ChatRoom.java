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
public class ChatRoom {
    private Long chatRoomId;
    private Long trainerId;
    private Long memberId;
    private LocalDateTime createdAt;
}
