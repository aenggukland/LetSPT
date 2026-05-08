package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Member", description = "회원 프로필 API — 조회/수정, 비밀번호 변경, 프로필 이미지, 회원 탈퇴 (JWT 인증 필수)")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "비밀번호를 제외한 회원 프로필 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@RequestAttribute("username") String username) {
        return ResponseEntity.ok(memberService.getMyInfo(username));
    }

    @Operation(summary = "내 정보 수정", description = "null이 아닌 필드만 선택적으로 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 오류")
    })
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@RequestAttribute("username") String username,
                                             @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMyInfo(username, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 일치 확인 후 새 비밀번호로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치")
    })
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestAttribute("username") String username,
                                               @RequestBody @Valid PasswordChangeRequest request) {
        memberService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 업로드", description = "이미지 파일(multipart/form-data)을 서버에 저장하고 접근 가능한 URL을 반환합니다. 최대 5MB.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일 형식 오류 또는 크기 초과")
    })
    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestAttribute("username") String username,
            @RequestPart("file") MultipartFile file) {
        String imageUrl = memberService.uploadProfileImage(username, file);
        return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
    }

    @Operation(summary = "프로필 이미지 삭제", description = "DB의 이미지 URL을 null로 초기화합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@RequestAttribute("username") String username) {
        memberService.deleteProfileImage(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "Refresh Token 삭제 후 소프트 삭제(is_deleted = TRUE) 처리합니다.")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@RequestAttribute("username") String username) {
        memberService.withdraw(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "트레이너 목록 조회", description = "활성 트레이너·마스터 전체 목록을 이름 오름차순으로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/trainers")
    public ResponseEntity<List<TrainerResponse>> getTrainerList() {
        return ResponseEntity.ok(memberService.getTrainerList());
    }

    @Operation(summary = "트레이너 단건 조회", description = "트레이너 ID로 공개 프로필을 조회합니다. 일반 회원 ID 입력 시 404를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "트레이너 없음")
    })
    @GetMapping("/trainers/{trainerId}")
    public ResponseEntity<TrainerResponse> getTrainerDetail(@PathVariable Long trainerId) {
        return ResponseEntity.ok(memberService.getTrainerDetail(trainerId));
    }
}
