package com.aenggukland.letspt.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                             @RequestBody MemberUpdateRequest request) {
        memberService.updateMyInfo(username, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestAttribute("username") String username,
                                               @RequestBody PasswordChangeRequest request) {
        memberService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@RequestAttribute("username") String username) {
        memberService.withdraw(username);
        return ResponseEntity.ok().build();
    }
}
