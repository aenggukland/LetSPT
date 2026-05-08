package com.aenggukland.letspt.dietfeedback;

import com.aenggukland.letspt.board.Board;
import com.aenggukland.letspt.board.BoardCategory;
import com.aenggukland.letspt.board.BoardMapper;
import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.fcm.FcmTokenService;
import com.aenggukland.letspt.fcm.FcmType;
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
public class DietFeedbackService {

    private final DietFeedbackMapper dietFeedbackMapper;
    private final MemberMapper memberMapper;
    private final BoardMapper boardMapper;
    private final FcmTokenService fcmTokenService;

    // 식단 피드백 등록 또는 수정: 이미 피드백이 있으면 타입만 변경, 없으면 신규 등록
    // DIET 카테고리 게시글에만 허용하며 TRAINER·MASTER만 사용 가능하다
    public void giveFeedback(String trainerUsername, Long boardId, DietFeedbackRequest request) {
        Member trainer = getByUsername(trainerUsername);
        MemberRole role = MemberRole.fromRoleId(trainer.getRoleId());
        if (role != MemberRole.TRAINER && role != MemberRole.MASTER) {
            throw new BusinessException(ErrorCode.DIET_FEEDBACK_ACCESS_DENIED);
        }

        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
        if (board.getCategory() != BoardCategory.DIET) {
            throw new BusinessException(ErrorCode.DIET_FEEDBACK_ACCESS_DENIED);
        }

        dietFeedbackMapper.findByBoardAndTrainer(boardId, trainer.getMemberId())
                .ifPresentOrElse(
                        existing -> dietFeedbackMapper.update(existing.getFeedbackId(), request.getType()),
                        () -> dietFeedbackMapper.save(DietFeedback.builder()
                                .boardId(boardId)
                                .trainerId(trainer.getMemberId())
                                .memberId(board.getAuthorId())
                                .type(request.getType())
                                .build())
                );

        fcmTokenService.sendPush(
                board.getAuthorId(),
                FcmType.DIET_FEEDBACK,
                trainer.getName() + "님이 식단에 " + request.getType().getLabel() + " 피드백을 남겼습니다.",
                boardId
        );
    }

    // 피드백 삭제: 등록한 트레이너 본인만 삭제 가능
    public void deleteFeedback(String trainerUsername, Long feedbackId) {
        Member trainer = getByUsername(trainerUsername);
        DietFeedback feedback = dietFeedbackMapper.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_FEEDBACK_NOT_FOUND));

        if (!feedback.getTrainerId().equals(trainer.getMemberId())) {
            throw new BusinessException(ErrorCode.DIET_FEEDBACK_ACCESS_DENIED);
        }

        dietFeedbackMapper.delete(feedbackId);
    }

    @Transactional(readOnly = true)
    public List<DietFeedbackResponse> getFeedbackByBoard(Long boardId) {
        return dietFeedbackMapper.findByBoardId(boardId);
    }

    private Member getByUsername(String username) {
        return memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
