package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 API — 회원가입, 로그인, 토큰 재발급, 로그아웃")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 운영: true(HTTPS 전용), 로컬: false(HTTP 허용) — application.yml의 cookie.secure 값
    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Operation(summary = "회원가입", description = "중복 아이디 검증 후 MEMBER 역할로 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "아이디 중복"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 오류")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        memberService.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "로그인", description = "인증 성공 시 accessToken(쿠키+본문)과 refreshToken(본문)을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request,
                                                     HttpServletResponse response) {
        Map<String, String> tokens = memberService.login(request);

        // SameSite=Strict: 크로스 사이트 요청 시 쿠키 전송 차단 (CSRF 방어)
        // Secure: 운영 환경에서 HTTPS 전용 전송 강제 (cookie.secure=true 시 활성화)
        ResponseCookie cookie = ResponseCookie.from("accessToken", tokens.get("accessToken"))
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60) // 1시간
                .secure(cookieSecure)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(tokens);
    }

    @Operation(summary = "토큰 재발급", description = "유효한 refreshToken으로 새 accessToken을 발급합니다. Body: {\"refreshToken\": \"...\"}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "refreshToken 만료 또는 유효하지 않음")
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String newAccessToken = memberService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "refreshToken을 무효화하고 accessToken을 블랙리스트에 등록합니다. Body: {\"refreshToken\": \"...\"}")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody Map<String, String> body) {
        // Authorization 헤더에서 Access Token 추출
        String accessToken = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            accessToken = header.substring(7);
        }

        memberService.logout(body.get("refreshToken"), accessToken);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증 확인", description = "JWT 인증이 정상적으로 동작하는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestAttribute("username") String username) {
        return ResponseEntity.ok("안녕하세요, " + username);
    }
}
