package com.aenggukland.letspt.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

// 인증된 회원의 프로필 조회/수정, 비밀번호 변경, 이미지 관리, 회원 탈퇴 API
// 모든 엔드포인트는 JWT 인증이 필요하며, @RequestAttribute("username")으로 인증 사용자를 수신한다
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 내 정보 조회: 비밀번호를 제외한 회원 프로필 정보를 반환한다
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@RequestAttribute("username") String username) {
        return ResponseEntity.ok(memberService.getMyInfo(username));
    }

    // 내 정보 수정: null이 아닌 필드만 선택적으로 업데이트한다
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@RequestAttribute("username") String username,
                                             @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMyInfo(username, request);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경: 현재 비밀번호 일치 확인 후 새 비밀번호로 교체한다
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestAttribute("username") String username,
                                               @RequestBody @Valid PasswordChangeRequest request) {
        memberService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    // 프로필 이미지 업로드: 이미지 파일을 서버에 저장하고 접근 가능한 URL을 반환한다
    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestAttribute("username") String username,
            @RequestPart("file") MultipartFile file) {
        String imageUrl = memberService.uploadProfileImage(username, file);
        return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
    }

    // 프로필 이미지 삭제: DB의 URL을 null로 초기화한다 (서버 파일은 미삭제, TODO B5)
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@RequestAttribute("username") String username) {
        memberService.deleteProfileImage(username);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴: Refresh Token 삭제 후 소프트 삭제(is_deleted = TRUE) 처리한다
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@RequestAttribute("username") String username) {
        memberService.withdraw(username);
        return ResponseEntity.ok().build();
    }
}
