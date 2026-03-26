package com.aenggukland.letspt.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

// 로그인 요청 DTO: 아이디와 비밀번호를 필수 입력값으로 받는다
@Getter
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
