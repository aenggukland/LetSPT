package com.aenggukland.letspt.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "영문, 숫자, 밑줄(_)만 사용 가능합니다.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "영문자와 숫자를 각각 1자 이상 포함해야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;
}
