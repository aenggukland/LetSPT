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
        return ResponseEntity.ok(memberService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String newAccessToken = memberService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        memberService.logout(body.get("refreshToken"));
        return ResponseEntity.ok().build();
    }

    // 인증 테스트용 엔드포인트
    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestAttribute("username") String username) {
        return ResponseEntity.ok("안녕하세요, " + username);
    }
}
