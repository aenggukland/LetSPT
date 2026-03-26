package com.aenggukland.letspt.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 회원가입 요청 DTO: 아이디·비밀번호·이름 입력값을 검증한다
@Getter
public class RegisterRequest {

    // 아이디: 영문·숫자·밑줄만 허용, 4~20자
    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "영문, 숫자, 밑줄(_)만 사용 가능합니다.")
    private String username;

    // 비밀번호: 영문자와 숫자를 각각 1자 이상 포함, 8~50자
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "영문자와 숫자를 각각 1자 이상 포함해야 합니다.")
    private String password;

    // 이름: 최대 50자
    @NotBlank
    @Size(max = 50)
    private String name;
}
