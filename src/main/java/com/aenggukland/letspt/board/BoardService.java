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

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;

    // ── my-page 카드용 ────────────────────────────────────────────────────

    public List<BoardSummary> getRecentLessons(Long memberId) {
        return boardMapper.findRecentLessonsByMemberId(memberId, 3);
    }

    public List<BoardSummary> getRecentDiets(Long memberId) {
        return boardMapper.findRecentByAuthor(memberId, BoardCategory.DIET.name(), 3);
    }

    public List<BoardSummary> getRecentExercises(Long memberId) {
        return boardMapper.findRecentByAuthor(memberId, BoardCategory.EXERCISE.name(), 3);
    }

    // ── 전체 목록 ─────────────────────────────────────────────────────────

    public List<Board> getLessonList(Long memberId) {
        return boardMapper.findAllLessonsByMemberId(memberId);
    }

    public List<Board> getDietList(Long authorId) {
        return boardMapper.findAllByAuthor(authorId, BoardCategory.DIET.name());
    }

    public List<Board> getExerciseList(Long authorId) {
        return boardMapper.findAllByAuthor(authorId, BoardCategory.EXERCISE.name());
    }

    // ── 단건 조회 ─────────────────────────────────────────────────────────

    public Board getDetail(Long boardId) {
        return boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

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
            targetMemberId = request.getMemberId();
        }

        boardMapper.save(Board.builder()
                .authorId(author.getMemberId())
                .memberId(targetMemberId)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .build());
    }

    public void update(String username, Long boardId, BoardUpdateRequest request) {
        Member author = getByUsername(username);
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getAuthorId().equals(author.getMemberId())) {
            throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
        }
        boardMapper.update(request, boardId);
    }

    public void delete(String username, Long boardId) {
        Member author = getByUsername(username);
        Board board = boardMapper.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getAuthorId().equals(author.getMemberId())) {
            throw new BusinessException(ErrorCode.BOARD_ACCESS_DENIED);
        }
        boardMapper.softDelete(boardId);
    }

    // ── private ───────────────────────────────────────────────────────────

    private Member getByUsername(String username) {
        return memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateWritePermission(BoardCategory category, MemberRole role) {
        if (category == BoardCategory.LESSON) {
            if (role != MemberRole.TRAINER && role != MemberRole.MASTER) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_WRITE);
            }
        } else {
            if (role != MemberRole.MEMBER) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_BOARD_WRITE);
            }
        }
    }
}
