package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "비밀번호 변경 요청")
@Getter
public class PasswordChangeRequest {

    @Schema(description = "현재 비밀번호", example = "oldPassword1")
    @NotBlank
    private String currentPassword;

    @Schema(description = "새 비밀번호 (영문자+숫자 각 1자 이상, 8~50자)", example = "newPassword2")
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "영문자와 숫자를 각각 1자 이상 포함해야 합니다.")
    private String newPassword;
}
