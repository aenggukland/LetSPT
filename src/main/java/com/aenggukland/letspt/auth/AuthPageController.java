package com.aenggukland.letspt.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 인증 페이지(Thymeleaf) 뷰 컨트롤러: 로그인·회원가입 화면을 반환한다
// SecurityConfig에서 이 경로들은 permitAll()로 인증 없이 접근 가능하다
@Controller
public class AuthPageController {

    // 루트(/) 및 로그인 페이지 반환
    @GetMapping({"/", "/login"})
    public String login() {
        return "auth/login";
    }

    // 회원가입 페이지 반환
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
}
