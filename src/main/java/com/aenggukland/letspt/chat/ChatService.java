package com.aenggukland.letspt.chat;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
