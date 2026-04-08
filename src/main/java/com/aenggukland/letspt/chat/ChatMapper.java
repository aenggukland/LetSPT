package com.aenggukland.letspt.chat;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {

    int makeChatRoom(ChatRoom chatRoom);

    List<ChatRoomListResponse> getMemberChatRoomList(Long memberId);

    List<ChatRoomListResponse> getTrainerChatRoomList(Long memberId);

    List<ChatDetailResponse> getChatDetailList(Long chatRoomId);

    Long getChatRoomMemberId(Long chatRoomId);

    Long getChatRoomTrainerId(Long chatRoomId);
}
