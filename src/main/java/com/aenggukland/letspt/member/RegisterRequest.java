package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "회원가입 요청")
@Getter
public class RegisterRequest {

    @Schema(description = "아이디 (영문·숫자·밑줄, 4~20자)", example = "john_doe")
    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "영문, 숫자, 밑줄(_)만 사용 가능합니다.")
    private String username;

    @Schema(description = "비밀번호 (영문자+숫자 각 1자 이상, 8~50자)", example = "password1")
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "영문자와 숫자를 각각 1자 이상 포함해야 합니다.")
    private String password;

    @Schema(description = "이름 (최대 50자)", example = "홍길동")
    @NotBlank
    @Size(max = 50)
    private String name;
}
