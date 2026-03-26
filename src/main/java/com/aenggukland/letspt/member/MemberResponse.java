package com.aenggukland.letspt.member;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 회원 정보 응답 DTO: 비밀번호, isDeleted 등 민감 정보를 제외하고 반환한다
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
    private String profileImageUrl;

    // Member 엔티티를 응답 DTO로 변환하는 정적 팩토리 메서드
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
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
