package com.aenggukland.letspt.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        memberService.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = memberService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 인증 테스트용 엔드포인트
    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestAttribute("username") String username) {
        return ResponseEntity.ok("안녕하세요, " + username);
    }
}
