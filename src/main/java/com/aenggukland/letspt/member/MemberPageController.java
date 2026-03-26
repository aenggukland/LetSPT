package com.aenggukland.letspt.member;

import com.aenggukland.letspt.board.BoardCategory;
import com.aenggukland.letspt.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 회원 페이지(Thymeleaf) 뷰 컨트롤러
// 마이페이지, 게시글 목록 등 서버사이드 렌더링 화면을 처리한다
// SecurityContextHolder에서 인증 정보를 직접 추출해 사용자를 식별한다 (TODO B4, B9)
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberPageController {

    private final MemberService memberService;
    private final BoardService boardService;

    // 회원 메인 페이지: 별도 데이터 없이 뷰만 반환한다
    @GetMapping("/main")
    public String main() {
        return "member/main";
    }

    // 마이페이지: 내 정보와 카테고리별 최근 게시글 3건을 함께 조회해 모델에 담는다
    @GetMapping("/my-page")
    public String myPage(Model model) {
        String username = getUsername();
        MemberResponse member = memberService.getMyInfo(username);
        model.addAttribute("member", member);
        model.addAttribute("recentLessons",  boardService.getRecentLessons(member.getMemberId()));
        model.addAttribute("recentDiets",    boardService.getRecentDiets(member.getMemberId()));
        model.addAttribute("recentExercises",boardService.getRecentExercises(member.getMemberId()));
        return "member/my-page";
    }

    // 마이페이지 편집: 현재 회원 정보를 폼에 미리 채워서 반환한다
    @GetMapping("/my-page/edit")
    public String myPageEdit(Model model) {
        model.addAttribute("member", memberService.getMyInfo(getUsername()));
        return "member/my-page-edit";
    }

    // 레슨 목록: 내가 대상 회원인 LESSON 게시글 전체를 조회한다
    @GetMapping("/board/lessons")
    public String lessons(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getLessonList(memberId));
        model.addAttribute("category", BoardCategory.LESSON.name());
        return "member/board-list";
    }

    // 식단 목록: 내가 작성한 DIET 게시글 전체를 조회한다
    @GetMapping("/board/diets")
    public String diets(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getDietList(memberId));
        model.addAttribute("category", BoardCategory.DIET.name());
        return "member/board-list";
    }

    // 운동 목록: 내가 작성한 EXERCISE 게시글 전체를 조회한다
    @GetMapping("/board/exercises")
    public String exercises(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getExerciseList(memberId));
        model.addAttribute("category", BoardCategory.EXERCISE.name());
        return "member/board-list";
    }

    // SecurityContextHolder에서 현재 인증된 사용자명을 추출하는 헬퍼
    // 인증 컨텍스트가 없으면 NPE 발생 가능 (TODO B4)
    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
