package com.aenggukland.letspt.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberPageController {

    @GetMapping("/main")
    public String main() {
        return "member/main";
    }

    @GetMapping("/my-page")
    public String myPage() {
        return "member/my-page";
    }
}
