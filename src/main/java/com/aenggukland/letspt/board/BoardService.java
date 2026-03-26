package com.aenggukland.letspt.board;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// 게시글 비즈니스 로직을 처리하는 서비스
// 카테고리별 작성 권한 검증, 작성자 소유권 확인, 마이페이지 카드용 최근 게시글 조회를 담당한다
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;

    // ── my-page 카드용 ────────────────────────────────────────────────────

    // 마이페이지 레슨 카드: 대상 회원(memberId) 기준으로 최근 3건을 조회한다
    public List<BoardSummary> getRecentLessons(Long memberId) {
        return boardMapper.findRecentLessonsByMemberId(memberId, 3);
    }

    // 마이페이지 식단 카드: 작성자(memberId) 기준으로 최근 3건을 조회한다
    public List<BoardSummary> getRecentDiets(Long memberId) {
        return boardMapper.findRecentByAuthor(memberId, BoardCategory.DIET.name(), 3);
    }

    // 마이페이지 운동 카드: 작성자(memberId) 기준으로 최근 3건을 조회한다
    public List<BoardSummary> getRecentExercises(Long memberId) {
        return boardMapper.findRecentByAuthor(memberId, BoardCategory.EXERCISE.name(), 3);
    }

    // ── 전체 목록 ─────────────────────────────────────────────────────────

    // LESSON 전체 목록: 대상 회원(memberId) 기준으로 전체를 최신순으로 조회한다
    public List<Board> getLessonList(Long memberId) {
        return boardMapper.findAllLessonsByMemberId(memberId);
    }

    // DIET 전체 목록: 작성자(authorId) 기준으로 전체를 최신순으로 조회한다
    public List<Board> getDietList(Long authorId) {
        return boardMapper.findAllByAuthor(authorId, BoardCategory.DIET.name());
    }

    // EXERCISE 전체 목록: 작성자(authorId) 기준으로 전체를 최신순으로 조회한다
    public List<Board> getExerciseList(Long authorId) {
        return boardMapper.findAllByAuthor(authorId, BoardCategory.EXERCISE.name());
    }

    // ── 단건 조회 ─────────────────────────────────────────────────────────

    // 게시글 단건 조회: 삭제된 게시글이거나 존재하지 않으면 BOARD_NOT_FOUND 예외를 던진다
    public Board getDetail(Long boardId) {
        return boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    // 게시글 생성: 카테고리별 작성 권한 검증 후 저장한다
    // LESSON 카테고리는 대상 회원(memberId)이 필수이며, 존재 여부 검증은 미구현 (TODO B7)
    public void create(String username, BoardCreateRequest request) {
        Member author = getByUsername(username);
        BoardCategory category = BoardCategory.valueOf(request.getCategory());
        MemberRole role = MemberRole.fromRoleId(author.getRoleId());

        validateWritePermission(category, role);

        Long targetMemberId = null;
        if (category == BoardCategory.LESSON) {
            if (request.getMemberId() == null) {
                throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
            }
            targetMemberId = request.getMemberId(); // LESSON만 대상 회원 ID를 저장
        }

        boardMapper.save(Board.builder()
                .authorId(author.getMemberId())
                .memberId(targetMemberId)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .build());
    }

    // 게시글 수정: 작성자 본인 확인 후 제목·내용을 업데이트한다
    public void update(String username, Long boardId, BoardUpdateRequest request) {
        Member author = getByUsername(username);
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getAuthorId().equals(author.getMemberId())) { // 작성자 본인만 수정 가능
            throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
        }
        boardMapper.update(request, boardId);
    }

    // 게시글 삭제: 작성자 본인 확인 후 소프트 삭제(is_deleted = TRUE)로 처리한다
    public void delete(String username, Long boardId) {
        Member author = getByUsername(username);
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getAuthorId().equals(author.getMemberId())) { // 작성자 본인만 삭제 가능
            throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
        }
        boardMapper.softDelete(boardId);
    }

    // ── private ───────────────────────────────────────────────────────────

    // 인증된 사용자명으로 Member 엔티티를 조회하는 공통 헬퍼
    // 존재하지 않으면 MEMBER_NOT_FOUND 예외를 던진다
    private Member getByUsername(String username) {
        return memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 카테고리별 작성 권한 검증
    // LESSON: TRAINER·MASTER만 작성 가능 / DIET·EXERCISE: 모든 역할 작성 가능
    private void validateWritePermission(BoardCategory category, MemberRole role) {
        if (category == BoardCategory.LESSON) {
            if (role != MemberRole.TRAINER && role != MemberRole.MASTER) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_WRITE);
            }
        }
        // DIET·EXERCISE는 MEMBER·TRAINER·MASTER 모두 작성 가능하므로 별도 검증 없음
    }
}
