package com.aenggukland.letspt.member;

import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
}
