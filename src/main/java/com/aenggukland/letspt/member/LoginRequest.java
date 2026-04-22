package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "로그인 요청")
@Getter
public class LoginRequest {

    @Schema(description = "아이디", example = "john_doe")
    @NotBlank
    private String username;

    @Schema(description = "비밀번호", example = "password1")
    @NotBlank
    private String password;
}
