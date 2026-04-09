package com.aenggukland.letspt.chat;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatMapper {

    int makeChatRoom(ChatRoom chatRoom);

    List<ChatRoomListResponse> getMemberChatRoomList(Long memberId);

    List<ChatRoomListResponse> getTrainerChatRoomList(Long memberId);

    List<ChatDetailResponse> getChatDetailList(Long chatRoomId);

    Long getChatRoomMemberId(Long chatRoomId);

    Long getChatRoomTrainerId(Long chatRoomId);

    Optional<Long> getChatSenderId(Long chatRoomId, Long chatId);

    int chatDelete(Long chatRoomId, Long chatId);

    void insertMessage(ChatMessage chatMessage);
}
