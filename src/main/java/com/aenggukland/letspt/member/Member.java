package com.aenggukland.letspt.member;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Member {

    private Long memberId;
    private Long roleId;
    private String username;
    private String name;
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bodyFatPercentage;
    private BigDecimal targetWeight;
    private String fitnessGoal;
    private String phoneNumber;
    private Boolean isDeleted;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime lastLoginAt;
}
