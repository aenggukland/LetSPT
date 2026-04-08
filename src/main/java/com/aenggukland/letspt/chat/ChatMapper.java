package com.aenggukland.letspt.chat;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMapper {

    int makeChatRoom(ChatRoom chatRoom);
}
