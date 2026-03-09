package com.aenggukland.letspt.member;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
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
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .name(member.getName())
                .gender(member.getGender())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .bodyFatPercentage(member.getBodyFatPercentage())
                .targetWeight(member.getTargetWeight())
                .fitnessGoal(member.getFitnessGoal())
                .phoneNumber(member.getPhoneNumber())
                .createdAt(member.getCreatedAt())
                .lastLoginAt(member.getLastLoginAt())
                .build();
    }
}
