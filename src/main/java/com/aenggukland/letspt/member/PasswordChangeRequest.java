package com.aenggukland.letspt.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 비밀번호 변경 요청 DTO: 현재 비밀번호 확인 후 새 비밀번호로 교체한다
@Getter
public class PasswordChangeRequest {

    // 현재 비밀번호: 일치 여부를 서비스 레이어에서 검증한다
    @NotBlank
    private String currentPassword;

    // 새 비밀번호: 영문자와 숫자를 각각 1자 이상 포함, 8~50자
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "영문자와 숫자를 각각 1자 이상 포함해야 합니다.")
    private String newPassword;
}
