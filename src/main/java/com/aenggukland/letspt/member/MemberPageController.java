package com.aenggukland.letspt.member;

import com.aenggukland.letspt.board.BoardCategory;
import com.aenggukland.letspt.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberPageController {

    private final MemberService memberService;
    private final BoardService boardService;

    @GetMapping("/main")
    public String main() {
        return "member/main";
    }

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

    @GetMapping("/my-page/edit")
    public String myPageEdit(Model model) {
        model.addAttribute("member", memberService.getMyInfo(getUsername()));
        return "member/my-page-edit";
    }

    @GetMapping("/board/lessons")
    public String lessons(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getLessonList(memberId));
        model.addAttribute("category", BoardCategory.LESSON.name());
        return "member/board-list";
    }

    @GetMapping("/board/diets")
    public String diets(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getDietList(memberId));
        model.addAttribute("category", BoardCategory.DIET.name());
        return "member/board-list";
    }

    @GetMapping("/board/exercises")
    public String exercises(Model model) {
        Long memberId = memberService.getMyInfo(getUsername()).getMemberId();
        model.addAttribute("boards",   boardService.getExerciseList(memberId));
        model.addAttribute("category", BoardCategory.EXERCISE.name());
        return "member/board-list";
    }

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
