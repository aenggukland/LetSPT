package com.aenggukland.letspt.chat;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatMapper chatMapper;
    private final MemberMapper memberMapper;

    // 트레이너가 채팅방 생성
    public void makeChatRoom(String username, Long memberId) {
        Member trainer = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole requestRole = MemberRole.fromRoleId(trainer.getRoleId());
        // 트레이너, 마스터 권한만 채팅방 개설 가능
        if(requestRole != MemberRole.MASTER && requestRole != MemberRole.TRAINER){
            throw new BusinessException(ErrorCode.CHAT_ROOM_MAKE_DENIED);
        }
        // 회원 정보 조회
        if(memberMapper.findById(memberId).isEmpty()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .trainerId(trainer.getMemberId())
                .memberId(memberId)
                .build();
        int makeChatRoomCnt = chatMapper.makeChatRoom(chatRoom);
        if(makeChatRoomCnt < 1){
            throw new BusinessException(ErrorCode.CHAT_ROOM_MAKE_FAILED);
        }

    }

    // 채팅방 조회
    public List<ChatRoomListResponse> getChatRoomList(String username) {
        Member requester = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole requesterRole = MemberRole.fromRoleId(requester.getRoleId());
        // 회원 채팅방 조회
        if(requesterRole == MemberRole.MEMBER){
            return chatMapper.getMemberChatRoomList(requester.getMemberId());
        // 트레이너, 마스터 채팅방 조회
        } else {
            return chatMapper.getTrainerChatRoomList(requester.getMemberId());
        }
    }

    // 채팅 조회
    public List<ChatDetailResponse> getChatDetailList(Long chatRoomId, String username) {
        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        MemberRole memberRole = MemberRole.fromRoleId(member.getRoleId());

        Long checkId = (memberRole == MemberRole.MEMBER ? chatMapper.getChatRoomMemberId(chatRoomId) : chatMapper.getChatRoomTrainerId(chatRoomId));
        if(!member.getMemberId().equals(checkId)){
            throw new BusinessException(ErrorCode.CHAT_ROOM_IN_DENIED);
        }

        return chatMapper.getChatDetailList(chatRoomId);
    }

    public void deleteChat(Long chatRoomId, Long chatId, String username) {
        Member member = memberMapper.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Long senderId = chatMapper.getChatSenderId(chatRoomId, chatId).orElseThrow(() -> new BusinessException(ErrorCode.DELETE_CHAT_NOT_FOUND));
        if(!member.getMemberId().equals(senderId)){
            throw new BusinessException(ErrorCode.CHAT_DELETE_DENIED);
        }

        int deleteChatCnt = chatMapper.chatDelete(chatRoomId, chatId);
        if(deleteChatCnt == 0){
            throw new BusinessException(ErrorCode.CHAT_DELETE_FAILED);
        }
    }
}
