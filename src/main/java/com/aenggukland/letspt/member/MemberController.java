package com.aenggukland.letspt.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@RequestAttribute("username") String username) {
        return ResponseEntity.ok(memberService.getMyInfo(username));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@RequestAttribute("username") String username,
                                             @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMyInfo(username, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestAttribute("username") String username,
                                               @RequestBody @Valid PasswordChangeRequest request) {
        memberService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestAttribute("username") String username,
            @RequestPart("file") MultipartFile file) {
        String imageUrl = memberService.uploadProfileImage(username, file);
        return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
    }

    @DeleteMapping("/me/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@RequestAttribute("username") String username) {
        memberService.deleteProfileImage(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@RequestAttribute("username") String username) {
        memberService.withdraw(username);
        return ResponseEntity.ok().build();
    }
}
