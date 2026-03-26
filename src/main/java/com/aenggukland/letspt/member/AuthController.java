package com.aenggukland.letspt.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 인증 관련 REST API 엔드포인트를 처리하는 컨트롤러
// 회원가입, 로그인, 토큰 재발급, 로그아웃 요청을 MemberService에 위임한다
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 회원가입: 중복 아이디 검증 후 MEMBER 역할로 계정을 생성한다
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        memberService.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인: 인증 성공 시 AccessToken을 HttpOnly 쿠키에 담고, 응답 본문으로도 반환한다
    // 쿠키 유효기간은 1시간이며 JWT 만료시간(30분)과 별개이다 (TODO B3)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request,
                                                     HttpServletResponse response) {
        Map<String, String> tokens = memberService.login(request);

        Cookie cookie = new Cookie("accessToken", tokens.get("accessToken"));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        response.addCookie(cookie);

        return ResponseEntity.ok(tokens);
    }

    // Access Token 재발급: 유효한 Refresh Token을 검증하고 새 Access Token을 반환한다
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String newAccessToken = memberService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // 로그아웃: Refresh Token을 DB에서 삭제해 재사용을 방지한다
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        memberService.logout(body.get("refreshToken"));
        return ResponseEntity.ok().build();
    }

    // 인증 테스트용 엔드포인트: JWT 필터를 통과한 사용자명을 그대로 반환한다
    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestAttribute("username") String username) {
        return ResponseEntity.ok("안녕하세요, " + username);
    }
}
